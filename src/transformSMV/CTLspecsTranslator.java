package transformSMV;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CTLspecsTranslator {

	public static String parentFolder = "aText2bip/CTLspecs/";
	private String modelName;
	private String trace_file;
	private String new_trace_file;


	public CTLspecsTranslator( String model ) {

		this.modelName = model;
		this.trace_file = parentFolder + "specs2-" + model;
		this.new_trace_file = parentFolder + "specsTr-" + model;
	}


	public static void main( String [ ] args ) throws IOException {

		String modelName = "CDMSstatus";
		CTLspecsTranslator c = new CTLspecsTranslator( modelName );
		c.translateSpecs( );
		// String input = " memrd_modmng.NuPread  memrd_modmng.NuPread";
		// Pattern p = Pattern.compile( "([\\w\\.]+)" );
		// Matcher m = p.matcher( input );
		//
		//
		// while ( m.find( ) ) {
		// System.out.println( m.group( ));
		// }

	}


	public void translateSpecs( ) throws IOException {

		SMVParser sParser = new SMVParser( parentFolder + this.modelName + ".smv" );
		sParser.readInteractionNames( );

		BufferedReader outR = new BufferedReader( new FileReader( trace_file ) );
		BufferedWriter outW = new BufferedWriter( new FileWriter( new_trace_file ) );

		String line;
		String out = "";
		while ( ( line = outR.readLine( ) ) != null ) {
			out = "";
			line = line.trim( );
			if ( line.startsWith( "////" ) || line.equals( "" ) || line.endsWith( ":" ) ) {
				outW.write( line + '\n' );
			} else {

				// find maximum word without characters (,),[.]. ! , U, &, |,
				// which are not the words AX, AG, X

				if ( line.startsWith( "FAIRNESS" ) ) {
					out = line;
				} else {

					outW.write( line + '\n' );
					out = "CTLSPEC " + line.split( ":" )[ 1 ];
				}

				Pattern p = Pattern.compile( "([\\w\\.0-9]+)" );
				Pattern pExclude = Pattern.compile( "^(AX|AG|A|U|FAIRNESS)$" );
				Matcher m = p.matcher( line );

				HashSet < String > tokens = new HashSet < String >( );

				while ( m.find( ) ) {
					String match = m.group( );
					if ( pExclude.matcher( match ).matches( ) ) {
						continue;
					}

					// if ( interaction_name.containsKey( match ) ) {
					tokens.add( match );

					// }
				}

				ArrayList<String> tokensList = new ArrayList<String>(tokens);
				tokensList.sort( new Comparator<String>(){

					@Override
					public int compare( String o1 , String o2 ) {

						return o2.length( )-o1.length( );
					}
					
				});
				
				for ( String match : tokensList ) {
					// don't translate if literal, happens in mode mng
					if ( match.contains( "." ) ) {
						continue;
					}
					String replacement = sParser.interaction_name.get( match );
					if ( replacement!=null ) {
						// remove the property template's code
						replacement = "(NuInteraction) = (" + sParser.interaction_name.get( match ) + ")";
						out = out.replaceAll( match , replacement );
						// System.out.println( match + "    "+replacement );
					}else{

						System.out.println(" Replacement for "+match+" was not found ");
					}
				}
				out += '\n';
				outW.write( out );

			}
		}

		outR.close( );
		outW.close( );

	}

}
