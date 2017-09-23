package text2bip;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.DefaultTemplateLexer;

public class AttrFiller {
	
	
	public static void addToTemplate( ArrayList < String > list , StringTemplate template , String var ) {

		ArrayList al = new ArrayList( list.size( ) );
		al.add( list );
		template.setAttribute( var , al );
	}


	public static void addToTemplate( String [ ] arr , StringTemplate template , String var ) {

		ArrayList al = new ArrayList( 1 );
		al.add( Arrays.asList( arr ) );
		template.setAttribute( var , al );
	}


	public static StringTemplate getTemplate( String groupFile , String tmplName ) throws Exception {

		StringTemplateGroup group;

		group = new StringTemplateGroup( new FileReader( groupFile ) , DefaultTemplateLexer.class );
		if(group.isDefinedInThisGroup( tmplName )){
			return group.getInstanceOf( tmplName );
		}else{
			return null;
		}

	}
	
	


}
