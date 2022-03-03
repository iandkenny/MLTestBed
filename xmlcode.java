StreamResult streamResult = new StreamResult(out);
		SAXTransformerFactory tf = (SAXTransformerFactory) SAXTransformerFactory.getDeclaredConstructor().newInstance();
		// SAX2.0 ContentHandler.
		TransformerHandler hd = tf.newTransformerHandler();
		Transformer serializer = hd.getTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
		serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,"users.dtd");
		serializer.setOutputProperty(OutputKeys.INDENT,"yes");
		hd.setResult(streamResult);
		hd.startDocument();
		AttributesImpl atts = new AttributesImpl();
		// USERS tag.
		hd.startElement("","","EXPERIMENT",atts);
		// USER tags.
		String[] id = {"PWD122","MX787","A4Q45"};
		String[] type = {"customer","manager","employee"};
		String[] desc = {"Tim@Home","Jack&Moud","John D'oé"};
		for (int i=0;i<id.length;i++)
		{
		  atts.clear();
		  atts.addAttribute("","","ID","CDATA",id[i]);
		  atts.addAttribute("","","TYPE","CDATA",type[i]);
		  hd.startElement("","","USER",atts);
		  hd.characters(desc[i].toCharArray(),0,desc[i].length());
		  hd.endElement("","","USER");
		}
		hd.endElement("","","EXPERIMENT");
		hd.endDocument();
