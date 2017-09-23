package transformSMV;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class NusmvTransform {

	public static String configFile = "aText2bip/modelTransformation/priorities.txt";
	public static String inputFile = "aText2bip/modelTransformation/smv_files/TCF4.smv";
	public static String outputFile = "aText2bip/modelTransformation/smv_files/TCF4_2.smv";
	public static HashMap < String , ArrayList < String >> low2highPrior = new HashMap < String , ArrayList < String >>( );


	public NusmvTransform() {

	}


	public static void main( String [ ] args ) throws IOException {

		BufferedReader br = new BufferedReader( new FileReader( configFile ) );

		String line = "";

		while ( ( line = br.readLine( ) ) != null ) {
			line = line.trim( );
			if ( line.equals( "" ) )
				continue;

			String [ ] parts = line.split( "<" );
			String low = parts[ 0 ].trim( );
			String high = parts[ 1 ].trim( );

			if ( !low2highPrior.containsKey( low ) ) {
				low2highPrior.put( low , new ArrayList < String >( 3 ) );
			}
			low2highPrior.get( low ).add( high );
		}

		br.close( );

		br = new BufferedReader( new FileReader( inputFile ) );
		BufferedWriter bw = new BufferedWriter( new FileWriter( outputFile ) );
		
		line = "";

		while ( ( line = br.readLine( ) ) != null ) {
			String oldLine = line;
			if ( ! line.startsWith( "		( (( (NuInteraction) =" ) ){
				bw.write(oldLine+'\n');
				continue;
			}
			
			String interaction = line.split( "->" )[1];
			interaction = interaction.split( "\\(" )[1];
			interaction = interaction.split( "\\)" )[0];
			
			if( !low2highPrior.containsKey( interaction )){
				bw.write(oldLine+'\n');
				continue;
			}

			ArrayList<String > higherInteractions = low2highPrior.get( interaction );
			String addition = "";
			for(String hInteraction : higherInteractions){
				addition += " & !("+hInteraction+")";
			}
			
			String newLine = oldLine.substring( 0, oldLine.length( )-3 );
			newLine += addition;
			newLine += oldLine.substring(oldLine.length( )-3);
			bw.write(newLine+'\n');
			
		}
		
		br.close( );
		bw.close( );

	}
}
