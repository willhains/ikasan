/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.topology.model;

import java.util.Date;
import java.util.Set;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class Module implements Comparable<Module>
{

    private Long id;
    private String name; 
    private String description;
    private String contextRoot;
    private String diagramUrl;
    private String version;
    private Set<ServerModule> serverModules;
    private Set<Flow> flows;

    /** The data time stamp when an instance was first created */
    private Date createdDateTime;

    /** The data time stamp when an instance was last updated */
    private Date updatedDateTime;

    /**
	 * Default constructor for Hibernate
	 */
    protected Module() {}

	/**
	 * Constructor 
	 * 
	 * @param name
	 * @param description
	 * @param server
	 */
	public Module(String name, String contextRoot, String description, String version, String diagramUrl)
	{
		super();
		this.name = name;
		this.contextRoot = contextRoot;
		this.description = description;
		this.version = version;
		this.diagramUrl = diagramUrl;
		
		long now = System.currentTimeMillis();
        this.createdDateTime = new Date(now);
        this.updatedDateTime = new Date(now);
	}

	/**
	 * @return the id
	 */
	public Long getId()
	{
		return id;
	}



	/**
	 * @param id the id to set
	 */
	public void setId(Long id)
	{
		this.id = id;
	}



	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}



	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}



	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}



	/**
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

    /**
     * @return the version
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version)
    {
        this.version = version;
    }

    /**
	 * @return the flows
	 */
	public Set<Flow> getFlows()
	{
		return flows;
	}



	/**
	 * @param flows the flows to set
	 */
	public void setFlows(Set<Flow> flows)
	{
		this.flows = flows;
	}

	
	/**
	 * @return the serverModules
	 */
	public Set<ServerModule> getServerModules() 
	{
		return serverModules;
	}

	/**
	 * @param serverModules the serverModules to set
	 */
	public void setServerModules(Set<ServerModule> serverModules) 
	{
		this.serverModules = serverModules;
	}

	/**
	 * 
	 * @param servers
	 */
	public void addServerModule(ServerModule server) 
	{
		this.serverModules.add(server);
	}

	/**
	 * 
	 * @return
	 */
	public Server getServerOnWhichActive()
	{
		for(ServerModule serverModule: this.serverModules)
		{
			if(serverModule.getStatus().equals("ACTIVE"))
			{
				return serverModule.getServer();
			}
		}
		
		return null;
	}

	/**
	 * @return the createdDateTime
	 */
	public Date getCreatedDateTime()
	{
		return createdDateTime;
	}

	/**
	 * @param createdDateTime the createdDateTime to set
	 */
	public void setCreatedDateTime(Date createdDateTime)
	{
		this.createdDateTime = createdDateTime;
	}



	/**
	 * @return the updatedDateTime
	 */
	public Date getUpdatedDateTime()
	{
		return updatedDateTime;
	}



	/**
	 * @param updatedDateTime the updatedDateTime to set
	 */
	public void setUpdatedDateTime(Date updatedDateTime)
	{
		this.updatedDateTime = updatedDateTime;
	}

	/**
	 * @return the diagramUrl
	 */
	public String getDiagramUrl()
	{
		return diagramUrl;
	}

	/**
	 * @param diagramUrl the diagramUrl to set
	 */
	public void setDiagramUrl(String diagramUrl)
	{
		this.diagramUrl = diagramUrl;
	}

	/**
	 * @return the contextRoot
	 */
	public String getContextRoot()
	{
		return contextRoot;
	}

	/**
	 * @param contextRoot the contextRoot to set
	 */
	public void setContextRoot(String contextRoot)
	{
		this.contextRoot = contextRoot;
	}

    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((contextRoot == null) ? 0 : contextRoot.hashCode());
		result = prime * result
				+ ((createdDateTime == null) ? 0 : createdDateTime.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((diagramUrl == null) ? 0 : diagramUrl.hashCode());
		result = prime * result + ((flows == null) ? 0 : flows.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((serverModules == null) ? 0 : serverModules.hashCode());
		result = prime * result
				+ ((updatedDateTime == null) ? 0 : updatedDateTime.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Module other = (Module) obj;
		if (contextRoot == null) {
			if (other.contextRoot != null)
				return false;
		} else if (!contextRoot.equals(other.contextRoot))
			return false;
		if (createdDateTime == null) {
			if (other.createdDateTime != null)
				return false;
		} else if (!createdDateTime.equals(other.createdDateTime))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (diagramUrl == null) {
			if (other.diagramUrl != null)
				return false;
		} else if (!diagramUrl.equals(other.diagramUrl))
			return false;
		if (flows == null) {
			if (other.flows != null)
				return false;
		} else if (!flows.equals(other.flows))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (serverModules == null) {
			if (other.serverModules != null)
				return false;
		} else if (!serverModules.equals(other.serverModules))
			return false;
		if (updatedDateTime == null) {
			if (other.updatedDateTime != null)
				return false;
		} else if (!updatedDateTime.equals(other.updatedDateTime))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}


	/**
     * Natural ordering on the name of the module
     * @param that the other Module to compare against
     * @return a negative integer, zero, or a positive integer as this object
     *          is less than, equal to, or greater than the specified object.
     */
	@Override
	public int compareTo(Module that)
	{
		return (this.name != null
                && that != null
                && that.name != null)
                    ? this.name.compareTo(that.name) : -1;
	}
}
