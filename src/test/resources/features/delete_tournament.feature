Feature: TC-51 - Eliminar Torneo en estado Finalizado
  Como Organizador
  Quiero eliminar permanentemente un torneo en estado Finalizado
  Para poder remover torneos que ya terminaron

  Scenario: Eliminar exitosamente un torneo en estado Finished
    Given a tournament exists with id "t-001" and status "FINISHED"
    When the organizer requests to delete tournament with id "t-001"
    Then the tournament should be deleted successfully

  Scenario: No se puede eliminar un torneo en estado Draft
    Given a tournament exists with id "t-002" and status "DRAFT"
    When the organizer requests to delete tournament with id "t-002"
    Then the deletion should be rejected with a business rule violation

  Scenario: No se puede eliminar un torneo en estado Active
    Given a tournament exists with id "t-003" and status "ACTIVE"
    When the organizer requests to delete tournament with id "t-003"
    Then the deletion should be rejected with a business rule violation

  Scenario: No se puede eliminar un torneo en estado In Preparation
    Given a tournament exists with id "t-004" and status "IN_PREPARATION"
    When the organizer requests to delete tournament with id "t-004"
    Then the deletion should be rejected with a business rule violation

  Scenario: No se puede eliminar un torneo en estado In Progress
    Given a tournament exists with id "t-005" and status "IN_PROGRESS"
    When the organizer requests to delete tournament with id "t-005"
    Then the deletion should be rejected with a business rule violation

  Scenario: No se puede eliminar un torneo que no existe
    Given no tournament exists with id "t-999"
    When the organizer requests to delete tournament with id "t-999"
    Then the deletion should fail with a not found error
