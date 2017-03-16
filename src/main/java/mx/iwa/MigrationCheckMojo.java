package mx.iwa;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "flyway-check")
public class MigrationCheckMojo extends AbstractMojo {
	@Parameter(defaultValue = "./src/main/resources/db/migration/")
	private File sql;

	@Parameter(defaultValue = "./src/main/java/db/migration/")
	private File java;

	@Parameter(defaultValue = "V")
	private String prefix;

	@Parameter(defaultValue = "__")
	private String separator;

	@Parameter
	private String value;

	List<String> identical = new LinkedList<String>();

	/* Key = migration version, value = list of paths to files that have that migration version. */
	Map<String, List<String>> versions = new HashMap<String, List<String>>();

	/** Adds java and sql migrations to 'versions' HashMap.
	 * if identical migration version is found, it is added to 'identical' list.
	 * @param file - file or folder to check for identical migration versions
	 * */ 
	private void check(File file){
		if (file.isFile() && isMigration(file.getName())) {
			String[] ar = file.getName().split(separator);
			if (versions.containsKey(ar[0])) {
				versions.get(ar[0]).add(file.getPath());
				identical.add(ar[0]);
			} else {
				List<String> list = new ArrayList<String>();
				list.add(file.getPath());
				versions.put(ar[0], list);
			}
		} else if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				check(f);
			}
		}
	}
	/** If identical migration versions were previously found, throws an error with the filepaths to them */
	private void check() throws MojoExecutionException {
		if (!identical.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (String v : identical) {
				sb.append(v);
				sb.append(":\n");
				List<String> ls = versions.get(v);
				for (String p : ls) {
					sb.append(p);
					sb.append("\n");
				}

			}
			throw new MojoExecutionException(
					"Flyway: Found more than one migration with the same version.\n" + sb.toString());
		}
	}

	private boolean isMigration(String name) {

		String fileName = name.toUpperCase();

		if (fileName.substring(0, 1).equals(prefix) && (fileName.endsWith(".SQL") || fileName.endsWith(".JAVA"))
				&& fileName.contains(separator)) {
			return true;
		} else {
			return false;
		}
	}

	public void execute() throws MojoExecutionException {
		if (!sql.exists()) {
			getLog().warn("Did not check for identical versions of SQL migrations. Folder " + sql.getPath()
					+ " does not exist.");
		} else {
			if (sql.isFile()) {
				getLog().warn(
						"Did not check for identical versions of SQL migrations. File path was given instead of a folder.");
			} else {
				check(sql);
			}
		}

		if (!java.exists()) {
			getLog().warn("Did not check for identical versions of JAVA migrations. Folder '" + java.getPath()
					+ "' does not exist.");
		} else {
			if (java.isFile()) {
				getLog().warn(
						"Did not check for identical versions of SQL migrations. File path was given instead of a folder.");
			} else {
				check(java);
			}
		}
		check();
	}
}