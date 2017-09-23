package transformSMV;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class SMVParser {

	private  String SMV_FILE = "aText2bip/CTLspecs/Payload.smv";
	
	private  String TRACE_FILE = "aText2bip/CTLspecs/specs";
	private  String NEW_TRACE_FILE = "aText2bip/CTLspecs/specs2";
	

	public HashMap < String , String > interaction_ports = new HashMap < String , String >( );
	public HashMap < String , String > name_interaction = new HashMap < String , String >( );
	public HashMap < String , String > interaction_name = new HashMap < String , String >( );


	public static void main( String [ ] args ) throws IOException {

		SMVParser sParser = new SMVParser();
		sParser.readInteractionNames( );
		sParser.transformNuSMVTrace( );

	}
	
	
	public SMVParser(){
		
	}
	
	public SMVParser(String smv_model){
		this.SMV_FILE = smv_model;
	}
	
	



	public void readInteractionNames( ) throws IOException {

		BufferedReader smvR = new BufferedReader( new FileReader( SMV_FILE ) );

		String line = "";

		boolean inModuleMain = false;
		boolean inDEFINE = false;
		boolean inINVAR = false;

		while ( ( line = smvR.readLine( ) ) != null ) {
			line = line.trim( );
			if ( line.equals( "" ) ) {
				continue;
			}

			if ( line.startsWith( "MODULE main" ) ) {
				inModuleMain = true;
				inDEFINE = false;
				inINVAR = false;
			} else if ( line.startsWith( "MODULE" ) ) {
				inModuleMain = false;
				inDEFINE = false;
				inINVAR = false;
			} else if ( line.equals( "DEFINE" ) ) {
				inDEFINE = true;
				inINVAR = false;
			} else if ( line.startsWith( "INVAR" ) ) {
				inDEFINE = false;
				inINVAR = true;
			} else if ( line.startsWith( "VAR" ) || line.startsWith( "INIT" ) || line.startsWith( "INVAR" ) || line.startsWith( "LTLSPEC" )
					|| line.startsWith( "SPEC" ) ) {
				inDEFINE = false;
				inINVAR = false;
			} else if ( inModuleMain && inDEFINE ) {
				// parse lines like:
				// SBAND_put := sband.NuPput;

				line = line.replaceAll( "\\s" , "" );
				line = line.replace( ";" , "" );
				String [ ] parts = line.split( ":=" );

				interaction_ports.put( parts[ 0 ] , parts[ 1 ] );

			} else if ( inModuleMain && inINVAR ) {
				// parse lines like:
				// ( (( (NuInteraction) = (NuI4) )) -> (SBAND_put) ) &
				if(line.endsWith( ";" )){
					inINVAR = false;
				}
				line = line.replaceAll( "\\s|\\)|\\(" , "" );
				line = line.substring( 0 , line.length( ) - 1 );
				String [ ] parts = line.split( "->|=" );
				String name = parts[ 2 ].split( "&" )[0];
				name_interaction.put( parts[ 1 ] , name );
				interaction_name.put(  name, parts[ 1 ] );

			}
		}

		smvR.close( );
	}
	
	
	public void transformNuSMVTrace( ) throws IOException {

		BufferedReader outR = new BufferedReader( new FileReader( TRACE_FILE ) );
		BufferedWriter outW = new BufferedWriter( new FileWriter( NEW_TRACE_FILE ) );

		String line;
		int count = 1;
		int loop_count = -1;
		String out = "";
		while ( ( line = outR.readLine( ) ) != null ) {
			line = line.trim( );
			if ( line.equals( "" ) ) {
				continue;
			}

			if ( line.contains( "Loop starts here" ) ) {
				out += "Loop starts here: " + count + '\n';
			} else if ( line.startsWith( "NuInteraction =" ) ) {
				line = line.replace( "NuInteraction = " , "" );
				String interaction = name_interaction.get( line );

				out += count + " " + interaction_ports.get( interaction ) + '\n';
				count++;
			}
		}

		outW.write( out );
		outR.close( );
		outW.close( );
	}

}
