package com.dictionary.dao;

import java.util.List;
import com.dictionary.dto.RootDTO;

public interface IRootDAO {
	
	RootDTO createRoot(RootDTO root);
	RootDTO getRootById(int id);
	RootDTO getRootByLetters(String rootLetters);
	List<RootDTO> getAllRoots();
	void updateRoot(RootDTO root);
	void deleteRoot(int id);
	
}