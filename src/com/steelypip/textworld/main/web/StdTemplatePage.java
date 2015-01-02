package com.steelypip.textworld.main.web;

import com.steelypip.powerups.minxml.MinXML;
import com.steelypip.textworld.main.World;

public abstract class StdTemplatePage extends TemplatePage {

	protected final MinXML template;
	protected final World world;

	public StdTemplatePage( final World world, final String template_name ) {
		this.world = world;
		this.template = WikiPage.fetchTemplate( template_name );
	}

	public MinXML getTemplate() {
		return template;
	}

}