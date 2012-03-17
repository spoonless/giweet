<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns="http://www.w3.org/1999/xhtml" xmlns:giweet="http://www.giweet.org/javadoc/1.0">
	<xsl:variable name="lang.generation.info" select="'Date de génération : '"/>
	<xsl:variable name="lang.java.class" select="'Documentation générée à partir de la classe Java : '"/>
	<xsl:variable name="lang.steps" select="'Instructions de test'"/>
	<xsl:variable name="lang.available.steps" select="'Instructions disponibles : '"/>
	<xsl:variable name="lang.steps.description" select="'Description des instructions'"/>
	<xsl:variable name="lang.step" select="'Instruction : '"/>
	<xsl:variable name="lang.step.method" select="'Cette instruction est résolue par la méthode '"/>
	
	<xsl:import href="giweet2html_en.xsl"/>
</xsl:stylesheet>