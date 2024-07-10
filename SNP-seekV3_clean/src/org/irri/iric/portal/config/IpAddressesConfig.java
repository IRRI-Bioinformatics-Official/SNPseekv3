package org.irri.iric.portal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:IpAddresses.properties")
public class IpAddressesConfig {

	@Value("${ip.galaxy}")
	private String galaxyIp;

	public String getGalaxyIp() {
		return galaxyIp;
	}

	public void setGalaxyIp(String galaxyIp) {
		this.galaxyIp = galaxyIp;
	}
	
	

}
