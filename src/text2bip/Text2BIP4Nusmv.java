package text2bip;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.stringtemplate.StringTemplate;

/**
 * The difference between this class and Text2BIP lies in the following: 1. No
 * parentheses for ports and components with zero parameters (new stg file) 2.
 * No internal transitions (all transitions are external
 * 
 * @author mania
 *
 */
public class Text2BIP4Nusmv {

	// public static final String TextualModel =
	// "docs/modelTransformation/textModel";
	// public static final String BIPModel =
	// "docs/modelTransformation/bipModel.bip";

	public static final String LABEL_DELIMITER = ":";
	public static final String groupFile = "aText2bip/text2bipOld.stg";

	public static final String modelsHome = "aText2bip/modelTransformation/";

	public static String configFile = "priorities.txt";


	public static void main( String [ ] args ) throws Exception {
		if( args.length > 0){
		text2bip( args[ 0 ] , groupFile);
		}else{
			text2bip( "TMF1F2_textModel" , groupFile);
		}

		// /for()
	}


	public static void text2bip( String input ,  String groupFile) throws Exception {
		
		String textualModel = "aText2bip/modelTransformation/" + input;
		String packageName = input.split( "_" )[ 0 ] + "_bipModel";
		String BIPModel = "aText2bip/modelTransformation/" + packageName + ".bip";

		BufferedReader r;
		r = new BufferedReader( new FileReader( textualModel ) );

		BufferedWriter w = new BufferedWriter( new FileWriter( BIPModel ) );
		
		AttrFiller fill = new AttrFiller( );
		StringTemplate template = null;
		String inside = "";


		String standardPortName = fill.getTemplate(groupFile , "portStdName").toString();
		
		Pattern inParenthesis = Pattern.compile( "(\\(.*\\))" );

		HashMap < String , HashMap > connectorTypes = new HashMap < String , HashMap >( );
		connectorTypes.put( "RDV2" , new HashMap( ) );
		connectorTypes.get( "RDV2" ).put( "numParams" , 2 );
		connectorTypes.get( "RDV2" ).put( "params" , new ArrayList < String >( ) );
		( ( ArrayList < String > ) connectorTypes.get( "RDV2" ).get( "params" ) ).add( standardPortName );

		connectorTypes.put( "RDVD2" , new HashMap( ) );
		connectorTypes.get( "RDV2" ).put( "exportType" , "aPort" );
		connectorTypes.get( "RDVD2" ).put( "numParams" , 2 );
		connectorTypes.get( "RDVD2" ).put( "params" , new ArrayList < String >( ) );
		( ( ArrayList < String > ) connectorTypes.get( "RDVD2" ).get( "params" ) ).add( standardPortName );



		
		boolean IN_PORTS = false;
		boolean IN_CONNECTORS = false;

		template = fill.getTemplate( groupFile , "header" );
		template.setAttribute( "name" , packageName );
		w.write( template.toString( ) );

		HashMap < String , Object > attributes = new HashMap < String , Object >( );
		HashMap < String , ArrayList > compType_expPorts = new HashMap < String , ArrayList >( );

		String line = "";
		int c = 0;
		String comment = "";
		String rootComponent = "";
		try {
			while ( ( line = r.readLine( ) ) != null ) {
				c++;
				line = line.trim( );
				// if line is a new component
				if ( line.startsWith( "$!" ) ) {
					// skip lines until end comment symbol '!$' is found
					int endCommentIndex = -1;
					while ( ( endCommentIndex = line.indexOf( "!$" ) ) == -1 && ( line = r.readLine( ) ) != null ) {
						c++;
					}
					if ( line != null ) {
						line = line.substring( endCommentIndex + 2 );
					}

					line = line.trim( );
				}
				if ( line.equals( "" ) ) {

				} else if ( line.startsWith( "documentation" + LABEL_DELIMITER ) ) {
					String type = line.split( LABEL_DELIMITER )[ 0 ];
					String documentation = line.split( LABEL_DELIMITER )[ 1 ].trim( );
					attributes.put( "documentation" , documentation );

				} else if ( line.startsWith( "atom" ) || line.startsWith( "compound" ) ) {
					line = line.replaceAll( "\\s+" , "" );
					String type = line.split( LABEL_DELIMITER )[ 0 ];
					String name = line.split( LABEL_DELIMITER )[ 1 ];
					rootComponent = name;
					template = fill.getTemplate( groupFile , type );
					attributes = new HashMap < String , Object >( );

					attributes.put( "name" , name );
					if ( type.equals( "atom" ) ) {
						attributes.put( "ports" , new ArrayList < HashMap >( ) );
						attributes.put( "places" , new HashSet < String >( ) );
						attributes.put( "portNames" , new HashMap < String , HashMap >( ) );
						attributes.put( "exportNames" , new HashMap < String , HashMap >( ) );
						attributes.put( "data" , new ArrayList < String [ ] >( ) );
						attributes.put( "initAct" , new ArrayList < String >( ) );
					}
					if ( type.equals( "compound" ) ) {
						
						attributes.put( "connectors" , new ArrayList < HashMap >( ) );
						attributes.put( "components" , new ArrayList < HashMap >( ) );
						attributes.put( "exportNames" , new HashMap < String , HashMap >( ) );
						attributes.put( "connectedPorts" , new HashSet < String >( ) );
					}

				} else if ( line.startsWith( "init:" ) ) {
					String init = line.split( LABEL_DELIMITER )[ 1 ];
					attributes.put( "init" , init );

				} else if ( line.startsWith( "initAct" + LABEL_DELIMITER ) ) {
					line = line.replace( "initAct" + LABEL_DELIMITER , "" ).trim( );
					String initAct = line;
					if ( !initAct.equals( "" ) ) {
						( ( ArrayList ) attributes.get( "initAct" ) ).add( initAct );
					}

				} else if ( line.startsWith( "data" + LABEL_DELIMITER ) ) {
					String type = line.split( LABEL_DELIMITER )[ 0 ];
					line = line.split( ":" )[ 1 ];
					if ( line != null ) {
						line = line.replaceAll( "\\s+" , "" );
						String [ ] data = line.split( "," );
						( ( ArrayList < Object > ) attributes.get( "data" ) ).add( data );
					}

				} else if ( line.startsWith( "ports" + LABEL_DELIMITER ) ) {
					String type = line.split( LABEL_DELIMITER )[ 0 ];
					IN_PORTS = true;

				} else if ( line.startsWith( "connectors" + LABEL_DELIMITER ) ) {
					String type = line.split( LABEL_DELIMITER )[ 0 ];
					IN_CONNECTORS = true;

				} else if ( line.startsWith( "component" + LABEL_DELIMITER ) ) {
					line = line.split( LABEL_DELIMITER )[ 1 ];
					line = line.replaceAll( "\\s+" , "" );
					String compType = line.split( "," )[ 0 ];
					String compName = line.split( "," )[ 1 ];

					HashMap < String , String > comp = new HashMap < String , String >( );
					comp.put( "type" , compType );
					comp.put( "name" , compName );

					( ( ArrayList ) attributes.get( "components" ) ).add( comp );

				} else if ( line.equals( "end" ) ) {
					IN_PORTS = false;
					IN_CONNECTORS = false;

					template.setAttribute( "name" , attributes.get( "name" ) );
					if ( template.getName( ).equals( "atom" ) ) {
						template.setAttribute( "documentation" , attributes.get( "documentation" ) );
						template.setAttribute( "data" , attributes.get( "data" ) );
						template.setAttribute( "portNames" , ( ( HashMap ) attributes.get( "portNames" ) ).values( ) );
						template.setAttribute( "exportNames" , ( ( HashMap ) attributes.get( "exportNames" ) ).values( ) );
						template.setAttribute( "init" , attributes.get( "init" ) );
						template.setAttribute( "initAct" , attributes.get( "initAct" ) );
						template.setAttribute( "places" , attributes.get( "places" ) );
						template.setAttribute( "ports" , attributes.get( "ports" ) );
						// save for later
						compType_expPorts.put( ( String ) attributes.get( "name" ) , new ArrayList());
						compType_expPorts.get( ( String ) attributes.get( "name" ) ).addAll( new ArrayList( ( ( HashMap ) attributes.get( "exportNames" ) ).values( ) ) );
						compType_expPorts.get( ( String ) attributes.get( "name" ) ).addAll( new ArrayList( ( ( HashMap ) attributes.get( "portNames" ) ).values( )  ) );	
		
					} else if ( template.getName( ).equals( "compound" ) ) {
						
						
						template.setAttribute( "documentation" , attributes.get( "documentation" ) );
						template.setAttribute( "components" , attributes.get( "components" ) );

						// connect unspecified ports of subcomponents using the
						// appropriate connector
						int comp = 0;
						for ( HashMap < String , String > component : ( ArrayList < HashMap > ) attributes.get( "components" ) ) {
							ArrayList < HashMap > compPorts = compType_expPorts.get( component.get( "type" ) );
							if ( compPorts != null ) {
								for ( HashMap < String , Object > export : compPorts ) {
									String exportName = ( String ) export.get( "exportName" );
									String compPortName = component.get( "name" ) + "." + exportName;
									if ( ! ( ( HashSet ) attributes.get( "connectedPorts" ) ).contains( compPortName ) ) {

										HashMap < String , Object > conn = new HashMap < String , Object >( );
										String connName = component.get( "name" ) + "_" + exportName;
										ArrayList connParams = new ArrayList( 1 );
										connParams.add( compPortName );
										conn.put( "name" , connName );
										conn.put( "params" , connParams );

										if ( export.get( "connType" ) != null ) {
											String prevConnType = ( String ) export.get( "connType" );

											String nextConnType = "SINGLE";
											if ( AttrFiller.getTemplate( groupFile , prevConnType + "_next" ) != null ) {
												nextConnType = AttrFiller.getTemplate( groupFile , prevConnType + "_next" ).toString( );
											} else if ( prevConnType.endsWith( "E" ) ) {
												nextConnType = prevConnType.substring( 0 , prevConnType.length( ) - 1 );
											}
											conn.put( "type" , nextConnType );
											( ( ArrayList ) attributes.get( "connectors" ) ).add( conn );
										} else {
											String [ ] portParams = ( String [ ] ) export.get( "params" );
											String connType = "SINGLE";
											if ( portParams != null ) {
												connType += portParams.length;
											}
											conn.put( "type" , connType );
											( ( ArrayList ) attributes.get( "connectors" ) ).add( conn );
										}

									}
								}
							}
						}

						addPriorities( template , (ArrayList) attributes.get( "connectors" ));
						template.setAttribute( "connectors" , attributes.get( "connectors" ) );
						template.setAttribute( "exportNames" , ( ( HashMap ) attributes.get( "exportNames" ) ).values( ) );
						// save for later
						// componentName -> [ , , ]
						compType_expPorts.put( ( String ) attributes.get( "name" ) ,
								new ArrayList( ( ( HashMap ) attributes.get( "exportNames" ) ).values( ) ) );

					}
					

					
					// template.setAttribute( "name" , name );
					w.write( template.toString( ) );

					attributes = new HashMap < String , Object >( );
				} else if ( line.startsWith( "/*" ) && line.endsWith( "*/" ) ) {

					comment = line;

				} else if ( IN_CONNECTORS ) {
					// String [ ] values = line.split( "," );
					HashMap < String , Object > conn = new HashMap < String , Object >( );
					if ( !comment.equals( "" ) ) {
						conn.put( "comment" , comment );
						comment = "";
					}
					line = line.replaceAll( "\\s+" , "" );

					int nextComma = line.indexOf( "," );
					String type = line.substring( 0 , nextComma );
					conn.put( "type" , type );
					line = line.substring( nextComma + 1 );

					nextComma = line.indexOf( "," );
					String name = line.substring( 0 , nextComma );
					conn.put( "name" , name );
					line = line.substring( nextComma + 1 );

					nextComma = line.indexOf( "," );
					String exportName = line.substring( 0 , nextComma );
					conn.put( "exportName" , exportName );
					line = line.substring( nextComma + 1 );

					ArrayList params = new ArrayList( );
					conn.put( "params" , params );
					while ( !line.equals( "" ) ) {
						nextComma = line.indexOf( "," );
						String param = "";
						if ( nextComma == -1 ) {
							param = line;
							line = "";
						} else {
							param = line.substring( 0 , nextComma );
							line = line.substring( nextComma + 1 );
						}
						params.add( param );
					}

					if ( !exportName.equals( "" ) ) {
						conn.put( "exportName" , exportName );
						if ( ! ( ( HashMap ) attributes.get( "exportNames" ) ).containsKey( exportName ) ) {
							HashMap < String , Object > exportName_portNames = new HashMap < String , Object >( );
							exportName_portNames.put( "names" , new ArrayList( ) );
							exportName_portNames.put( "exportName" , exportName );
							exportName_portNames.put( "connType" , type );
							String paramsNum = type;
							( ( HashMap ) attributes.get( "exportNames" ) ).put( exportName , exportName_portNames );
						}
						( ( ArrayList ) ( ( HashMap ) ( ( HashMap ) attributes.get( "exportNames" ) ).get( exportName ) ).get( "names" ) ).add( conn );
					}

					( ( ArrayList ) attributes.get( "connectors" ) ).add( conn );
					// for optimization
					( ( HashSet ) attributes.get( "connectedPorts" ) ).addAll( params );

				} else if ( IN_PORTS ) {
					HashMap < String , Object > port = new HashMap < String , Object >( );
					( ( ArrayList < HashMap > ) attributes.get( "ports" ) ).add( port );

					int nextComma = line.indexOf( "," );
					String from = line.substring( 0 , nextComma ).trim( );
					port.put( "from" , from );
					line = line.substring( nextComma + 1 ).trim( );
					( ( HashSet ) attributes.get( "places" ) ).add( from );

					nextComma = line.indexOf( "," );
					String to = line.substring( 0 , nextComma ).trim( );
					port.put( "to" , to );
					line = line.substring( nextComma + 1 ).trim( );
					( ( HashSet ) attributes.get( "places" ) ).add( to );

					boolean withExport = false;

					if ( line.startsWith( "*" ) ) {
						line = line.substring(1,line.length( ));
						port.put( "internal" , true );
					} else {
						withExport = true;
					}

					String portString = "";
					// split to closing parenthesis before comma
					if ( line.split( "," ).length > 0 && line.split( "," )[ 0 ] != null && line.split( "," )[ 0 ].contains( "(" ) ) {
						portString = line.substring( 0 , line.indexOf( ")" ) + 1 );
					} else if ( line.split( "," ).length == 0 && line.contains( "(" ) ) {
						portString = line.substring( 0 , line.indexOf( ")" ) + 1 );
					} else if ( line.contains( "," ) ) {
						portString = line.substring( 0 , line.indexOf( "," ) );
					} else {
						portString = line;
					}
					// find the appropriate comma that splits the port
					line = line.replace( portString , "" );
					nextComma = line.indexOf( "," );
					line = line.substring( nextComma + 1 ).trim( );

					// if action contains ( then find its closing ending
					String portNameString = portString;

					Matcher m = inParenthesis.matcher( portString );
					String [ ] params = null;
					String paramsString = "";
					if ( m.find( ) ) {
						paramsString = m.group( 1 );
						portNameString = portString.replace( paramsString , "" );
						paramsString = paramsString.substring( 1 , paramsString.length( ) - 1 );
						params = paramsString.split( "," );
						port.put( "params" , params );

						if ( params.length > 0 ) {
							StringTemplate t = fill.getTemplate( groupFile , "portStdName" );
							t.setAttribute( "int" , params.length );
							String portType = t.toString( );
							port.put( "portType" , portType );
						}
					}
					if ( !portNameString.equals( "" ) ) {
						port.put( "name" , portNameString );
						//if ( withExport ) {
							port.put( "exportName" , portNameString );
						//}
					}

					// //////////////////// add to attributes : START
					// ///////////////////////

					String mapEntry = "portNames";
					if ( withExport ) {
						mapEntry = "exportNames";

					}
					if ( port.containsKey( "name" ) && !port.get( "name" ).equals( "" )
							&& ! ( ( HashMap ) attributes.get( mapEntry ) ).containsKey( port.get( "name" ) ) ) {
						( ( HashMap ) attributes.get( mapEntry ) ).put( ( String ) port.get( "name" ) , port );
					}

					// //////////////////// add to attributes : END
					// ///////////////////////

					// //////////////////// add to attributes : END
					// ///////////////////////

					nextComma = line.indexOf( "," );
					String cond = "";
					if ( nextComma != -1 ) {
						cond = line.substring( 0 , nextComma ).trim( );
						line = line.substring( nextComma + 1 ).trim( );
					} else {
						cond = line;
						line = "";
					}

					if ( !cond.equals( "" ) ) {
						port.put( "cond" , cond );
					}

					String act = line;
					if ( !act.equals( "" ) ) {
						port.put( "act" , act );
					}

				} else if ( line.equals( "" ) ) {

				} else {
					System.out.println( "No case found for line: Possible error in line " + c );
				}

			}

		} catch ( Exception e ) {
			// TODO Auto-generated catch block
			System.out.println( "Error found in line " + c );
			e.printStackTrace( );
		}

		template = fill.getTemplate( groupFile , "footer" );
		template.setAttribute( "rootComponent" , rootComponent );

		w.write( template.toString( ) );

		w.close( );
		r.close( );
	}
	
	
	public static void addPriorities(StringTemplate template , ArrayList<HashMap> connectors) throws IOException{
		
		HashSet < String > connectorNames = new HashSet<String>();
		for(HashMap conn : connectors){
			connectorNames.add( (String) conn.get( "name" ) );
		}
		
		BufferedReader br = new BufferedReader( new FileReader( modelsHome + configFile ) );
		HashMap < String , ArrayList < String >> low2highPrior = new HashMap < String , ArrayList < String >>( );

		String line = "";

		while ( ( line = br.readLine( ) ) != null ) {
			line = line.trim( );
			if ( line.equals( "" ) )
				continue;

			String [ ] parts = line.split( "<" );
			String low = parts[ 0 ].trim( );
			String high = parts[ 1 ].trim( );
			
			if(! connectorNames.contains( low ) || ! connectorNames.contains( high )){
				continue;
			}

			HashMap<String, String> priorityStructure = new HashMap<String, String>();
			priorityStructure.put( "name" , low+"_after_"+high );
			priorityStructure.put( "low" , low );
			priorityStructure.put( "high" , high );
			template.setAttribute( "priorities" , priorityStructure );
			
		}

		br.close( );
	}
}