package de.unimainz.imbei.mzid.webservice;

import javax.xml.bind.annotation.XmlAttribute;

public class PIDDto {
	private String pid;
	
	public PIDDto() {}
	
	PIDDto(String v) {
		pid = v;
	}

	@XmlAttribute
	public String getPid() {
		return pid;
	}
	
	public void setPid(String pid) {
		this.pid = pid;
	}
}
