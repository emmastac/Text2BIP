package transformSMV;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class CTLSpecsCreator {

	public static String parentFolder = "aText2bip/CTLspecs/";
	private String modelName;
	private String arch_file;
	private String ctl_specs_file;
	
	private HashSet<String> fairness = new HashSet<String>();


	public static void main( String [ ] args ) throws IOException {
		String modelName = "Payload";
		CTLSpecsCreator c = new CTLSpecsCreator( modelName );
		c.constructSpecs( );
		
		CTLspecsTranslator t = new CTLspecsTranslator( modelName );
		t.translateSpecs( );

	}

	public CTLSpecsCreator(String model){
		this.modelName = model;
		this.arch_file = parentFolder+"specs-"+model;
		this.ctl_specs_file = parentFolder+"specs2-"+model;
	}

	public void constructSpecs( ) throws IOException {

		BufferedReader outR = new BufferedReader( new FileReader( arch_file ) );
		BufferedWriter outW = new BufferedWriter( new FileWriter( ctl_specs_file ) );

		String line;
		String out = "";
		while ( ( line = outR.readLine( ) ) != null ) {
			line = line.trim( );
			if ( line.startsWith( "////" ) ) {
				continue;
			} else if ( line.equals( "" ) ) {
				outW.write( '\n' );
				continue;
			}

			line = line.replace( "Architecture:" , "" ).trim( );
			if ( constructMutex( outR , outW , line ) == true ) {

			} else if ( constructActionFlowAbort( outR , outW , line ) == true ) {

			} else if ( constructActionFlow( outR , outW , line ) == true ) {

			} else if ( constructModeManagement( outR , outW , line ) == true ) {

			} else if ( constructClientServer( outR , outW , line ) == true ) {

			}

		}
		
		outW.write( '\n');
		//add fairness constraints
		for( String fair : fairness ) {
			outW.write( "FAIRNESS ( "+fair+" );"+'\n' );
		}

		//outW.write( out );
		outR.close( );
		outW.close( );

	}


	public boolean constructModeManagement( BufferedReader outR , BufferedWriter outW , String line ) throws IOException {

		String architectureTag = "MD";

		if ( !line.startsWith( architectureTag ) ) {
			return false;
		}
		String coordinator = "";
		String out = "";

		ArrayList < String [ ] > modesActs = new ArrayList < String [ ] >( );

		while ( ( line = outR.readLine( ) ) != null ) {
			line = line.trim( );
			if ( line.startsWith( "////" ) | line.equals( "" ) ) {
				continue;
			} else if ( line.startsWith( "coordinator:" ) ) {
				coordinator = line.replace( "coordinator:" , "" ).trim( );
			} else if ( line.startsWith( "end" ) ) {
				break;
			} else if ( line.startsWith( "mode-act:" ) ) {
				String [ ] modeActs = line.split( ":" )[ 1 ].trim( ).split( "," );
				modesActs.add( modeActs );
			}

		}
		out += "-- " + coordinator + ": " + '\n';

		// for each mode : if actBegin , then inMode
		for ( String [ ] modeActs : modesActs ) {
			String ORacts = compose( "|" , Arrays.copyOfRange( modeActs , 1 , modeActs.length ) );
			String md1 = "AG ( " + ORacts + " -> " + modeActs[ 0 ] + " )";
			out += "-- MD1: " + md1 + '\n';
			outW.write( out );
		}
		

		return true;

	}


	public boolean constructClientServer( BufferedReader outR , BufferedWriter outW , String line ) throws IOException {

		String architectureTag = "CS";

		if ( !line.startsWith( architectureTag ) ) {
			return false;
		}
		String coordinator = "";
		String out = "";

		ArrayList < String [ ] > usesActs = new ArrayList < String [ ] >( );

		while ( ( line = outR.readLine( ) ) != null ) {
			line = line.trim( );
			if ( line.startsWith( "////" ) | line.equals( "" ) ) {
				continue;
			} else if ( line.startsWith( "coordinator:" ) ) {
				coordinator = line.replace( "coordinator:" , "" ).trim( );
			} else if ( line.startsWith( "end" ) ) {
				break;
			} else if ( line.startsWith( "use-act:" ) ) {
				String [ ] useActs = line.split( ":" )[ 1 ].trim( ).split( "," );
				usesActs.add( useActs );
			}

		}
		out += "-- " + coordinator + ": " + '\n';

		// TODO
		return true;

	}


	public boolean constructMutex( BufferedReader outR , BufferedWriter outW , String line ) throws IOException {

		String architectureTag = "MX";

		if ( !line.startsWith( architectureTag ) ) {
			return false;
		}
		String coordinator = "";
		String out = "";

		String [ ] take = null;
		String [ ] release = null;

		while ( ( line = outR.readLine( ) ) != null ) {
			line = line.trim( );
			if ( line.startsWith( "////" ) | line.equals( "" ) ) {
				continue;
			} else if ( line.startsWith( "coordinator:" ) ) {
				coordinator = line.replace( "coordinator:" , "" ).trim( );
			} else if ( line.startsWith( "end" ) ) {
				break;
			} else if ( line.startsWith( "take:" ) ) {
				take = line.replace( "take:" , "" ).trim( ).split( "," );
			} else if ( line.startsWith( "release:" ) ) {
				release = line.replace( "release:" , "" ).trim( ).split( "," );
			}

		}

		// for each take : after take , no other take , until release
		String ORtakes = compose( "|" , take );
		String ORreleases = compose( "|" , release );
		String mx1 = alwaysIfThenNotUntil( ORtakes , ORtakes , ORreleases );

		out += "-- " + coordinator + ": " + '\n';
		out += "-- MX1: " + mx1 + '\n';
		outW.write( out );

		return true;

	}


	public String alwaysIfThenNotUntil( String ifs , String thenNot , String until ) {
		fairness.add( until );
		String ret =  "AG ( " + ifs + " -> AX A [! (" + thenNot + ") U " + until + " ])";
		return ret;
	}


	public String alwaysNotUntil( String thenNot , String until ) {
		fairness.add( until );
		String ret = "AG ( A [! (" + thenNot + ") U " + until + " ])";
		return ret;
	}


	public String compose( String operator , String... operand ) {

		String out = "";
		for ( String op : operand ) {
			if ( !out.equals( "" ) ) {
				out += " " + operator + " ";
			}
			out += op;
		}
		if ( operand.length > 1 ) {
			out = "( " + out + " )";
		}
		return out;

	}


	public boolean constructActionFlow( BufferedReader outR , BufferedWriter outW , String line ) throws IOException {

		String architectureTag = "AF";

		if ( !line.startsWith( architectureTag ) ) {
			return false;
		}
		String coordinator = "";
		String out = "";

		String start = null;
		String finish = null;
		ArrayList < String [ ] > acts = new ArrayList < String [ ] >( 3 );

		while ( ( line = outR.readLine( ) ) != null ) {
			line = line.trim( );
			if ( line.startsWith( "////" ) | line.equals( "" ) ) {
				continue;
			} else if ( line.startsWith( "coordinator:" ) ) {
				coordinator = line.replace( "coordinator:" , "" ).trim( );
			} else if ( line.startsWith( "end" ) ) {
				break;
			}

			else if ( line.startsWith( "start:" ) ) {
				start = line.split( ":" )[ 1 ].trim( );
			} else if ( line.startsWith( "finish:" ) ) {
				finish = line.split( ":" )[ 1 ].trim( );
			} else if ( line.startsWith( "act-" ) ) {
				String [ ] act = line.split( ":" )[ 1 ].trim( ).split( "," );
				acts.add( act );
			}

		}

		out += "-- " + coordinator + ": " + '\n';
		// AF1. for each act : Always not actBegin_i until actEnd_i-1
		String af1 = "";
		for ( int i = 1 ; i < acts.size( ) ; i++ ) {
			String actEndPrev = compose( "|" , acts.get( i - 1 )[ 1 ] );
			String actBegin_i = acts.get( i )[ 0 ];
			af1 = alwaysIfThenNotUntil( start , actBegin_i , actEndPrev );
			out += "-- AF1: " + af1 + '\n';
		}

		// AF2. for each act : after actBegin_i , no actBegin_i until start
		String af2 = "";
		for ( int i = 0 ; i < acts.size( ) ; i++ ) {
			String actBegin_i = acts.get( i )[ 0 ];
			af2 = alwaysIfThenNotUntil( actBegin_i , actBegin_i , start );
			out += "-- AF2: " + af2 + '\n';
		}

		// AF3. after start , no finish until actEnd_n
		String actEnd_n = compose( "|" , acts.get( acts.size( ) - 1 )[ 1 ] );
		String af3 = alwaysIfThenNotUntil( start , finish , actEnd_n );
		out += "-- AF3: " + af3 + '\n';

		outW.write( out );

		return true;
	}


	public boolean constructActionFlowAbort( BufferedReader outR , BufferedWriter outW , String line ) throws IOException {

		String architectureTag = "AFA";

		if ( !line.startsWith( architectureTag ) ) {
			return false;
		}
		String coordinator = "";
		String out = "";

		String start = null;
		String finish = null;
		ArrayList < String [ ] > acts = new ArrayList < String [ ] >( 3 );

		while ( ( line = outR.readLine( ) ) != null ) {
			line = line.trim( );
			if ( line.startsWith( "////" ) | line.equals( "" ) ) {
				continue;
			} else if ( line.startsWith( "coordinator:" ) ) {
				coordinator = line.replace( "coordinator:" , "" ).trim( );
			} else if ( line.startsWith( "end" ) ) {
				break;
			}

			else if ( line.startsWith( "start:" ) ) {
				start = line.split( ":" )[ 1 ].trim( );
			} else if ( line.startsWith( "finish:" ) ) {
				finish = line.split( ":" )[ 1 ].trim( );
			} else if ( line.startsWith( "act-" ) ) {
				String [ ] act = line.split( ":" )[ 1 ].trim( ).split( "," );
				acts.add( act );
			}

		}

		out += "-- " + coordinator + ": " + '\n';
		// AFΑ1. for each act : after start , no actBegin_i until actEnd_i-1
		String af1 = "";
		for ( int i = 1 ; i < acts.size( ) ; i++ ) {
			String actEndPrev = compose( "|" , acts.get( i - 1 )[ 1 ] , acts.get( i - 1 )[ 2 ] );
			String actBegin_i = acts.get( i )[ 0 ];
			af1 = alwaysIfThenNotUntil( start , actBegin_i , actEndPrev );
			out += "-- AFΑ1: " + af1 + '\n';
		}

		// AFΑ2. for each act : after actBegin_i , no actBegin_i until start
		String af2 = "";
		for ( int i = 0 ; i < acts.size( ) ; i++ ) {
			String actBegin_i = acts.get( i )[ 0 ];
			af2 = alwaysIfThenNotUntil( actBegin_i , actBegin_i , start );
			out += "-- AFΑ2: " + af2 + '\n';
		}

		// AFΑ3. after start , no finish until actEnd_n
		String actEnd_n = compose( "|" , acts.get( acts.size( ) - 1 )[ 1 ] , acts.get( acts.size( ) - 1 )[ 2 ] );
		String af3 = alwaysIfThenNotUntil( start , finish , actEnd_n );
		out += "-- AFΑ3: " + af3 + '\n';

		// AFΑ4. for each act : after actEnd_i , no actAbort_i until actBegin_i
		String af4 = "";
		for ( int i = 0 ; i < acts.size( ) ; i++ ) {
			String [ ] act = acts.get( i );
			af4 = alwaysIfThenNotUntil( act[ 1 ] , act[ 2 ] , act[ 0 ] );
			out += "-- AFΑ4: " + af2 + '\n';
		}

		// AFΑ5. for each act : after actAbort_i , no actEnd_i until actBegin_i
		String af5 = "";
		for ( int i = 0 ; i < acts.size( ) ; i++ ) {
			String [ ] act = acts.get( i );
			af5 = alwaysIfThenNotUntil( act[ 2 ] , act[ 1 ] , act[ 0 ] );
			out += "-- AF5: " + af5 + '\n';
		}

		// AFΑ6. for each act : after actAbort_i , no actBegin_1 until start
		String af6 = "";
		for ( int i = 0 ; i < acts.size( ) ; i++ ) {
			af6 = alwaysIfThenNotUntil( acts.get( i )[ 2 ] , acts.get( 0 )[ 0 ] , start );
			out += "-- AFΑ6: " + af6 + '\n';
		}

		outW.write( out );

		return true;
	}
}
