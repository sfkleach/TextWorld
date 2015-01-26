package com.steelypip.textworld.main.web;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import com.steelypip.powerups.minxml.MinXML;

abstract class TemplatePage {
	public abstract @NonNull MinXML getTemplate();
	public abstract @NonNull Map< String, @Nullable MinXML > environment( Parameters parameters );
}