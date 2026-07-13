Feature: Inscribir equipo a un torneo (Payment Service)
  Como capitán de equipo
  Quiero inscribir a mi equipo en un torneo activo
  Para reservar un cupo mientras se confirma el pago con el Payment Service

  Scenario: Inscripción exitosa reserva el cupo y crea la orden de pago
    Given an enrollment tournament "t-201" exists in status "ACTIVE" with 8 slots and 0 reservations
    And the team "team1" has 9 registered players named "Los Tigres"
    And the payment service accepts the order creation
    When the captain enrolls team "team1" in tournament "t-201"
    Then the enrollment should be created in status "RESERVED"

  Scenario: Roster fuera del rango permitido rechaza la inscripción
    Given an enrollment tournament "t-202" exists in status "ACTIVE" with 8 slots and 0 reservations
    And the team "team2" has 5 registered players named "Los Novatos"
    When the captain enrolls team "team2" in tournament "t-202"
    Then the enrollment should be rejected with a roster size error

  Scenario: Torneo que no está Activo rechaza la inscripción
    Given an enrollment tournament "t-203" exists in status "DRAFT" with 8 slots and 0 reservations
    And the team "team3" has 9 registered players named "Los Halcones"
    When the captain enrolls team "team3" in tournament "t-203"
    Then the enrollment should be rejected because the tournament is not active

  Scenario: Sin cupos disponibles rechaza la inscripción
    Given an enrollment tournament "t-204" exists in status "ACTIVE" with 1 slots and 1 reservations
    And the team "team4" has 9 registered players named "Los Zorros"
    When the captain enrolls team "team4" in tournament "t-204"
    Then the enrollment should be rejected because there are no available slots

  Scenario: Falla del Payment Service revierte la reserva
    Given an enrollment tournament "t-205" exists in status "ACTIVE" with 8 slots and 0 reservations
    And the team "team5" has 9 registered players named "Los Osos"
    And the payment service fails to create the order
    When the captain enrolls team "team5" in tournament "t-205"
    Then the enrollment should be rejected because the payment order could not be created
    And no reservation should remain for team "team5" in tournament "t-205"
