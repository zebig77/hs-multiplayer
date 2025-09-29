package org.zebig.hs.utils

import groovy.json.JsonSlurper

println "aaa   aaa".replaceAll(/\s+/,' ')

def f = new File('data/hs_cards.json')
def slurper = new JsonSlurper()
def result = slurper.parseText(f.text)

result.cards
		.findAll { it.category != "hero" && it.category != "ability" }
		.findAll { it.set != "missions" && it.set != "promotion" }
		.sort { it.name }
		.each {
			println it.name
			println "     "+it.description
			def s = it.description
				.replaceAll("<b>", "")
				.replaceAll("</b>", "")
				.replaceAll(/\.$/, "")
			println "     $s"
		}