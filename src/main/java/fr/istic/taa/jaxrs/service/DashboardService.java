package fr.istic.taa.jaxrs.service;

import fr.istic.taa.jaxrs.dao.classic.AccountDAO;
import fr.istic.taa.jaxrs.dao.classic.ClientDAO;
import fr.istic.taa.jaxrs.dao.classic.GroupeDAO;
import fr.istic.taa.jaxrs.dao.classic.MessageDAO;
import fr.istic.taa.jaxrs.dto.DashboardDTO;
import fr.istic.taa.jaxrs.entity.Users;

public class DashboardService {

    private final AccountDAO accountDAO = new AccountDAO();
    private final GroupeDAO  groupeDAO  = new GroupeDAO();
    private final MessageDAO messageDAO = new MessageDAO();
    private final ClientDAO  clientDAO  = new ClientDAO();

    public DashboardDTO getStats(Long userId) {
        Users user = accountDAO.findUserById(userId);
        if (user == null) throw new RuntimeException("Utilisateur introuvable");

        long nbGroupes   = groupeDAO.countByUserId(userId);
        long nbMessages  = messageDAO.countSentByUserId(userId);
        long nbClients   = clientDAO.countByUserId(userId);

        return new DashboardDTO(userId, nbGroupes, nbMessages, nbClients);
    }
}