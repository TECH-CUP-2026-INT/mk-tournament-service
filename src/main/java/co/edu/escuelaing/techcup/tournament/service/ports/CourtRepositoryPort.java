package co.edu.escuelaing.techcup.tournament.service.ports;

import co.edu.escuelaing.techcup.tournament.service.Court;

public interface CourtRepositoryPort {
    Court save(Court court);
}
