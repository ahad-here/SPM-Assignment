package com.dictionary.bo;

import java.util.List;

import com.dictionary.dao.IRootDAO;
import com.dictionary.dto.RootDTO;

public class RootService {
	private IRootDAO iRootDAO;

	public RootService(IRootDAO iRootDAO) {
		this.iRootDAO = iRootDAO;
	}

	public RootDTO addRoot(String rootLetters) {
		if (rootLetters == null || rootLetters.trim().isEmpty()) {
			throw new IllegalArgumentException("Root letters cannot be empty.");
		}

		RootDTO existingRoot = iRootDAO.getRootByLetters(rootLetters);
		if (existingRoot != null) {
			return existingRoot;
		}

		RootDTO newRoot = new RootDTO(0, rootLetters);
		return iRootDAO.createRoot(newRoot);
	}

	public RootDTO getRoot(int id) {
		return iRootDAO.getRootById(id);
	}

	public List<RootDTO> browseAllRoots() {
		return iRootDAO.getAllRoots();
	}

	public void deleteRoot(Integer id) {
		iRootDAO.deleteRoot(id);
	}

	public void updateRootLetters(Integer id, String newRootLetters) {
		if (newRootLetters == null || newRootLetters.trim().length() < 3 || newRootLetters.trim().length() > 4) {
			throw new IllegalArgumentException("Updated root must be 3 or 4 letters long.");
		}

		RootDTO existingByLetters = iRootDAO.getRootByLetters(newRootLetters);
		if (existingByLetters != null && existingByLetters.getId() != id) {
			throw new IllegalStateException("Cannot update root: another root already uses these letters.");
		}
		RootDTO rootToUpdate = new RootDTO(id, newRootLetters.trim());
		iRootDAO.updateRoot(rootToUpdate);
	}
}