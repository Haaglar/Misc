import java.io.File;

public class Rename {

	public static void main(String[] args) 	{
		if(args.length == 0)
		{
			return;
		}
		String replaceValue ="";
		if(args.length == 2)
		{
			replaceValue =  args[1];
		}
		try
		{
			File[] files = new File(".").listFiles();
			if(files.length != 0)
			{
				for (File file : files){
				    if (file.isFile() && !file.getName().equals("PtnRena.class")) {
				        String name = file.getName();
				        String oldName = name;
				        name = name.replace(args[0], replaceValue);
				        if(!oldName.equals(name) && !(new File(name).isFile()))
				        {
				        	file.renameTo(new File(name));
				        }
				    }
				}
			}
			else
			{
				System.out.println("No file found in current directory");
			}	
		}
		catch(SecurityException eSecurityException)
		{
			System.out.println("Security access error");
		}	
	}
}
