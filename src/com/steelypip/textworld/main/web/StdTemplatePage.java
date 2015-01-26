package com.steelypip.textworld.main.web;

import java.io.File;
import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import com.steelypip.powerups.minxml.MinXML;
import com.steelypip.textworld.main.World;

public abstract class StdTemplatePage extends TemplatePage {

	protected final @NonNull MinXML template;
	protected final File game_folder;

	public StdTemplatePage( final File game_folder, final String template_name ) {
		this.game_folder = game_folder;
		this.template =  WikiPage.fetchTemplate( template_name );
	}

	public @NonNull MinXML getTemplate() {
		return template;
	}

}