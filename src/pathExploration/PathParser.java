package pathExploration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.StyledEditorKit.ForegroundAction;


public class PathParser {

	// the state is the set of all components' place and variable values.
	public static HashMap <String , Integer> states_numbers = new HashMap <String , Integer>( );
	public static HashMap <Integer, ArrayList <Integer>> childStates = new HashMap <Integer , ArrayList <Integer>>( );
	
	


	public static void parsePath ( String bipOutputPath ) throws IOException {

		// read each state, place them in the HashSet
		BufferedReader br = new BufferedReader( new FileReader( bipOutputPath ) );

		String line = null;

		// find contiguous strings in square brackets, which is not 'BIP ENGINE'
		Pattern squareBrackets = Pattern.compile( "(\\[[^]]+\\])+" );
		Matcher m;

		while ( ( line = br.readLine( ) ) != null ) {
			m = squareBrackets.matcher( line );
			while ( m.find( ) ) {
				String serializedState = m.group( );

				serializedState = serializedState.replace( "[BIP ENGINE]" , "" );
				if ( serializedState.equals( "" ) ) {
					continue;
				}
				System.out.println( serializedState );

				if( ! states_numbers.keySet( ).contains( serializedState ) ){
					states_numbers.put( serializedState , states_numbers.keySet( ).size( ) );
				}
				
			}
		}
		
		String[] statesArray = new String[states_numbers.keySet( ).size( )];
		
		for( String state : states_numbers.keySet( ) ){
			statesArray[ states_numbers.get( state ) ] = state;
		}
		
		

	}


	public static void main ( String [ ] args ) throws IOException {

		parsePath( "bip_sample/lala" );

	}

}
