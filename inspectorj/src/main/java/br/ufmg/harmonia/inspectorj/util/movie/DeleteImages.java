package br.ufmg.harmonia.inspectorj.util.movie;

import java.io.File;

import br.ufmg.harmonia.inspectorj.util.GraphSingleton;

public class DeleteImages {

	public void deleteImages(){
		File directory = new File("images/movie");
		File[] listFiles = directory.listFiles();
		for (File file : listFiles) {
			if(file.isFile() && file.getName().toUpperCase().endsWith("PNG")){
				file.delete();							
			}
		}
		
		GraphSingleton.getInstance().setMovieIsCanceled(false);		
	}

}
