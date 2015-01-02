package com.steelypip.textworld.main.web;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jdt.annotation.Nullable;

import com.steelypip.powerups.alert.Alert;
import com.steelypip.powerups.minxml.FlexiMinXML;
import com.steelypip.powerups.minxml.MinXML;
import com.steelypip.powerups.minxson.MinXSON;
import com.steelypip.textworld.gameclasses.GameObject;
import com.steelypip.textworld.main.Main;
import com.steelypip.textworld.main.World;

class ObjectPage extends StdTemplatePage {
	
	ObjectPage( final World world, final String template_name ) {
		super( world, template_name );
	}
	
	public Map< String, @Nullable MinXML > environment( Map< String, List< String > > parameters ) {
		final Map< String, @Nullable MinXML > environment = new TreeMap<>();
		environment.put( "version", MinXSON.newString( Main.getVersion() ) );
		
		return environment;		
	}
}