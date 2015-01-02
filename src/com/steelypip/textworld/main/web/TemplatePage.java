package com.steelypip.textworld.main.web;

import java.util.List;
import java.util.Map;

import org.eclipse.jdt.annotation.Nullable;

import com.steelypip.powerups.minxml.MinXML;

abstract class TemplatePage {
	public abstract MinXML getTemplate();
	public abstract Map< String, @Nullable MinXML > environment( Map< String, List< String > > parameters );
}