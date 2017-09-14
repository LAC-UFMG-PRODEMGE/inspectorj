package br.ufmg.harmonia.inspectorj.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import br.ufmg.harmonia.inspectorj.InspectorSTPMain;



@Mojo( name = "run" ,requiresDependencyResolution = ResolutionScope.COMPILE, requiresDependencyCollection = ResolutionScope.COMPILE)
public class InspectJMojo extends AbstractMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		
//		final PluginDescriptor pluginDescriptor = (PluginDescriptor) getPluginContext().get("pluginDescriptor");
//		
//		Arrays.asList(pluginDescriptor.getClass())
//			 .stream()
//			 .map(Object::toString)
//			 .forEach(getLog()::info);

		InspectorSTPMain.main(null);
		
		getLog().info("---- End InspectorJ ----");
	}

}
