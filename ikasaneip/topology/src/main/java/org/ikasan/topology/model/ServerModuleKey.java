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

import java.io.Serializable;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class ServerModuleKey implements Serializable
{
	private static final long serialVersionUID = -4421374165447714949L;
	
	private Long serverId;
	private Long moduleId;
	
	@SuppressWarnings("unused")
	private ServerModuleKey(){}
	
	public ServerModuleKey(Long serverId, Long moduleId) 
	{
		super();
		this.serverId = serverId;
		this.moduleId = moduleId;
	}

	/**
	 * @return the serverId
	 */
	public Long getServerId() {
		return serverId;
	}
	
	/**
	 * @param serverId the serverId to set
	 */
	public void setServerId(Long serverId) {
		this.serverId = serverId;
	}
	
	/**
	 * @return the moduleId
	 */
	public Long getModuleId() {
		return moduleId;
	}
	
	/**
	 * @param moduleId the moduleId to set
	 */
	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((moduleId == null) ? 0 : moduleId.hashCode());
		result = prime * result
				+ ((serverId == null) ? 0 : serverId.hashCode());
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
		ServerModuleKey other = (ServerModuleKey) obj;
		if (moduleId == null) {
			if (other.moduleId != null)
				return false;
		} else if (!moduleId.equals(other.moduleId))
			return false;
		if (serverId == null) {
			if (other.serverId != null)
				return false;
		} else if (!serverId.equals(other.serverId))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ServerModuleKey [serverId=" + serverId + ", moduleId="
				+ moduleId + "]";
	}
}
