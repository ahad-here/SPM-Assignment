package com.dictionary.pl;

import com.dictionary.dao.IRootDAO;
import com.dictionary.dao.SqlRootDAO;
import com.dictionary.bo.RootService;

public class app {

	private static RootService setupAndGetService() throws Exception {
		IRootDAO rootDAO = new SqlRootDAO();
		return new RootService(rootDAO);
	}

	public static void main(String[] args) {
		try {

			RootService service = setupAndGetService();
			RootManagementUI.setRootService(service);
			RootManagementUI.launch(RootManagementUI.class, args);

		} catch (Exception e) {
			System.err.println("FATAL ERROR: Application failed to start or inject dependencies.");
			e.printStackTrace();
		}
	}
}