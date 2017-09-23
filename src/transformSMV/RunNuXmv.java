package transformSMV;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class RunNuXmv {

	public RunNuXmv() {

		// TODO Auto-generated constructor stub
	}


	public static void main( String [ ] args ) throws IOException, InterruptedException {
		new RunNuXmv().usingBash( );
	}


	public void createScript( ) throws IOException {

		String s = "";

		s += "go_bmc" + '\n';
		// for each property
		BufferedReader br = new BufferedReader( new FileReader( "src/other/LTLproperties" ) );
		
		String property = "";
		while( ( property = br.readLine() ) !=null ){
			
			property = property.trim( );
			if(property.equals( "" )){
				continue;
			}
			s += "echo \"" + property + "\"" + '\n';
			s += "check_ltlspec_sbmc_inc -p \"" + property + "\"" + '\n';

		}
		s += "quit" + '\n';
		br.close( );
		
		BufferedWriter bw = new BufferedWriter( new FileWriter( "src/other/cmd.txt" ) );
		bw.write( s );
		bw.close( );

	}


	public void usingBash( ) throws IOException, InterruptedException {
		createScript( ) ;
		
		ProcessBuilder pb = new ProcessBuilder("/home/dsg003/Tools/NuSMV-2.6.0-Linux/bin/NuSMV", " -int -source src/other/cmd.txt src/other/TCModel.smv");
		pb.redirectErrorStream( true );
				 Process p = pb.start();
				 p.waitFor( );
			
				 
		
	}


	public void totallyProgrammatically( ) {

		// read model
		// String command = "/home/mania/Tools/NuSMV-2.6.0-Linux/bin/NuSMV";
		// ProcessBuilder pb = new ProcessBuilder(command,
		// "-int src/other/TCModel.smv");
		// Process p = pb.start();
		// int out1 = p.waitFor( );
		//
		// p = pb.command("go" ).start();
		// out1 = p.waitFor( );
		// System.out.println( out1 );
		// run a property and output to file

		// final NusmvLibrary nusmvLibrary = new NuSMV4J().getNusmvLibrary();
		// nusmvLibrary.CmdOpenFile( "-int src/other/TCModel.smv" );
		// nusmvLibrary.Cmd_CommandExecute(toByteBuffer("reset"));
		// nusmvLibrary.Cmd_CommandExecute(toByteBuffer("set default_trace_plugin 4"));
		// nusmvLibrary.Cmd_CommandExecute(toByteBuffer("read_model -i testModel.nusmv"));
		// nusmvLibrary.Cmd_CommandExecute(toByteBuffer("go"));
		// nusmvLibrary.Cmd_CommandExecute(toByteBuffer("check_ctlspec -p \"!x\""));
		// nusmvLibrary.Cmd_CommandExecute(toByteBuffer("show_traces"));
	}

}
