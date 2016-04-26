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
package org.ikasan.dashboard.ui.topology.component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.ErrorOccurrencePopup;
import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.icons.AtlassianIcons;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.framework.window.TextWindow;
import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.history.model.CustomMetric;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.ikasan.spec.history.MessageHistoryEvent;
import org.ikasan.spec.history.MessageHistoryService;
import org.ikasan.spec.search.PagedSearchResult;
import org.ikasan.spec.wiretap.WiretapEvent;
import org.ikasan.topology.model.BusinessStream;
import org.ikasan.topology.model.BusinessStreamFlow;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.wiretap.dao.WiretapDao;
import org.tepi.filtertable.FilterTable;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect.ItemDescriptionGenerator;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class MetricsTab extends TopologyTab
{
	private Logger logger = Logger.getLogger(MetricsTab.class);
	
	private FilterTable messageHistoryTable;

	private PopupDateField errorFromDate;
	private PopupDateField errorToDate;
	
	private ComboBox businessStreamCombo;
	
	private float splitPosition;
	private Unit splitUnit;
	
	private IndexedContainer container = null;
	
	private PlatformConfigurationService platformConfigurationService;
	
	private MessageHistoryService<FlowInvocationContext, PagedSearchResult<MessageHistoryEvent>> messageHistoryService; 
	
	private Label resultsLabel = new Label();
	
	private HorizontalLayout searchResultsSizeLayout = new HorizontalLayout();
	
	private String errorClipboard;
	private String jiraClipboard;
	
	private PagedSearchResult<MessageHistoryEvent> messageHistoryEvents;
	
	private WiretapDao wiretapService;
	
	public MetricsTab(MessageHistoryService<FlowInvocationContext, PagedSearchResult<MessageHistoryEvent>> messageHistoryService,
			PlatformConfigurationService platformConfigurationService, WiretapDao wiretapService)
	{
		this.messageHistoryService = messageHistoryService;
		if(this.messageHistoryService == null)
		{
			throw new IllegalArgumentException("messageHistoryService cannot be null!");
		}

		this.platformConfigurationService = platformConfigurationService;
		if(this.platformConfigurationService == null)
		{
			throw new IllegalArgumentException("platformConfigurationService cannot be null!");
		}
		
		this.wiretapService = wiretapService;
		if(this.wiretapService == null)
		{
			throw new IllegalArgumentException("wiretapService cannot be null!");
		}

	}
	
	protected IndexedContainer buildContainer() 
	{
		IndexedContainer cont = new IndexedContainer();

		cont.addContainerProperty("Metric Location", Layout.class,  null);
		cont.addContainerProperty("Event Id", String.class,  null);
		cont.addContainerProperty("Metrics", TextArea.class,  null);
		cont.addContainerProperty("Start Time", String.class,  null);	
		cont.addContainerProperty("End Time", String.class,  null);	
		cont.addContainerProperty(" ", Button.class,  null);

        return cont;
    }
	
	public void createLayout()
	{		
		container = buildContainer();
		this.messageHistoryTable = new FilterTable();
		this.messageHistoryTable.setFilterBarVisible(true);
		this.messageHistoryTable.setSizeFull();
		this.messageHistoryTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
		this.messageHistoryTable.addStyleName(ValoTheme.TABLE_SMALL);
		this.messageHistoryTable.addStyleName("ikasan");
		this.messageHistoryTable.setContainerDataSource(container);
		this.messageHistoryTable.setColumnExpandRatio("Metric Location", .1f);
		this.messageHistoryTable.setColumnExpandRatio("Event Id", .2f);
		this.messageHistoryTable.setColumnExpandRatio("Metrics", .45f);
		this.messageHistoryTable.setColumnExpandRatio("Start Time", .1f);
		this.messageHistoryTable.setColumnExpandRatio("End Time", .1f);
		this.messageHistoryTable.setColumnExpandRatio("", .05f);
		
		this.messageHistoryTable.addStyleName("wordwrap-table");
		
		this.messageHistoryTable.addItemClickListener(new ItemClickEvent.ItemClickListener() 
		{
		    @Override
		    public void itemClick(ItemClickEvent itemClickEvent) 
		    {
//		    	if (itemClickEvent.isDoubleClick())
//		    	{
//			    	ErrorOccurrence errorOccurrence = (ErrorOccurrence)itemClickEvent.getItemId();
//			    	ErrorOccurrenceViewWindow errorOccurrenceViewWindow = new ErrorOccurrenceViewWindow(errorOccurrence, errorReportingManagementService,
//			    			platformConfigurationService);
//			    	
//			    	UI.getCurrent().addWindow(errorOccurrenceViewWindow);
//		    	}
		    }
		});
		
		this.messageHistoryTable.setItemDescriptionGenerator(new ItemDescriptionGenerator() 
		{                             
			@Override
			public String generateDescription(com.vaadin.ui.Component source,
					Object itemId, Object propertyId)
			{
				 return "Double click the table row to view details of metric!";
			}
		});
				
		Button searchButton = new Button("Search");
		searchButton.setStyleName(ValoTheme.BUTTON_SMALL);
		searchButton.addClickListener(new Button.ClickListener() 
    	{
            @SuppressWarnings("unchecked")
			public void buttonClick(ClickEvent event) 
            {
            	refreshTable(true, null);
            }
        });
		
		Button clearButton = new Button("Clear");
		clearButton.setStyleName(ValoTheme.BUTTON_SMALL);
		clearButton.addClickListener(new Button.ClickListener() 
    	{
            public void buttonClick(ClickEvent event) 
            {
            	modules.removeAllItems();
            	flows.removeAllItems();
            	components.removeAllItems();
            }
        });

		GridLayout layout = new GridLayout(1, 6);
		layout.setMargin(false);
		layout.setHeight(270 , Unit.PIXELS);
		
		super.initialiseFilterTables();
		
		GridLayout listSelectLayout = new GridLayout(3, 1);
		listSelectLayout.setSpacing(true);
		listSelectLayout.setSizeFull();
		listSelectLayout.addComponent(super.modules, 0, 0);
		listSelectLayout.addComponent(super.flows, 1, 0);
		listSelectLayout.addComponent(super.components, 2, 0);
				
		GridLayout dateSelectLayout = new GridLayout(2, 1);

		dateSelectLayout.setSizeFull();
		errorFromDate = new PopupDateField("From date");
		errorFromDate.setResolution(Resolution.MINUTE);
		errorFromDate.setValue(this.getMidnightToday());
		errorFromDate.setDateFormat(DashboardConstants.DATE_FORMAT_CALENDAR_VIEWS);
		dateSelectLayout.addComponent(errorFromDate, 0, 0);
		errorToDate = new PopupDateField("To date");
		errorToDate.setResolution(Resolution.MINUTE);
		errorToDate.setValue(this.getTwentyThreeFixtyNineToday());
		errorToDate.setDateFormat(DashboardConstants.DATE_FORMAT_CALENDAR_VIEWS);
		dateSelectLayout.addComponent(errorToDate, 1, 0);
				
		
		final VerticalSplitPanel vSplitPanel = new VerticalSplitPanel();
		vSplitPanel.setHeight("95%");
		
		GridLayout searchLayout = new GridLayout(2, 1);
		searchLayout.setSpacing(true);
		searchLayout.addComponent(searchButton, 0, 0);
		searchLayout.addComponent(clearButton, 1, 0);
		
		final Button hideFilterButton = new Button();
		hideFilterButton.setIcon(VaadinIcons.MINUS);
		hideFilterButton.setCaption("Hide Filter");
		hideFilterButton.setStyleName(ValoTheme.BUTTON_LINK);
		hideFilterButton.addStyleName(ValoTheme.BUTTON_SMALL);
		
		final Button showFilterButton = new Button();
		showFilterButton.setIcon(VaadinIcons.PLUS);
		showFilterButton.setCaption("Show Filter");
		showFilterButton.addStyleName(ValoTheme.BUTTON_LINK);
		showFilterButton.addStyleName(ValoTheme.BUTTON_SMALL);
		showFilterButton.setVisible(false);

		final HorizontalLayout hListSelectLayout = new HorizontalLayout();
		hListSelectLayout.setHeight(150 , Unit.PIXELS);
		hListSelectLayout.setWidth("100%");
		hListSelectLayout.addComponent(listSelectLayout);
		
		final HorizontalLayout hDateSelectLayout = new HorizontalLayout();
		hDateSelectLayout.setHeight(40, Unit.PIXELS);
		hDateSelectLayout.setWidth("100%");
		hDateSelectLayout.addComponent(dateSelectLayout);
		
		final HorizontalLayout hSearchLayout = new HorizontalLayout();
		hSearchLayout.setHeight(30 , Unit.PIXELS);
		hSearchLayout.setWidth("100%");
		hSearchLayout.addComponent(searchLayout);
		hSearchLayout.setComponentAlignment(searchLayout, Alignment.MIDDLE_CENTER);
		
		hideFilterButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {	
            	hideFilterButton.setVisible(false);
            	showFilterButton.setVisible(true);
            	splitPosition = vSplitPanel.getSplitPosition();
            	splitUnit = vSplitPanel.getSplitPositionUnit();
            	vSplitPanel.setSplitPosition(0, Unit.PIXELS);
            }
        });

		
		showFilterButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {	
            	hideFilterButton.setVisible(true);
            	showFilterButton.setVisible(false);
            	vSplitPanel.setSplitPosition(splitPosition, splitUnit);
            }
        });
		
		GridLayout filterButtonLayout = new GridLayout(2, 1);
		filterButtonLayout.setHeight(25, Unit.PIXELS);
		filterButtonLayout.addComponent(hideFilterButton, 0, 0);
		filterButtonLayout.addComponent(showFilterButton, 1, 0);
		
		Label filterHintLabel = new Label();
		filterHintLabel.setCaptionAsHtml(true);
		filterHintLabel.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() + 
				" Drag items from the topology tree to the tables below in order to narrow your search.");
		filterHintLabel.addStyleName(ValoTheme.LABEL_TINY);
		filterHintLabel.addStyleName(ValoTheme.LABEL_LIGHT);
		
		layout.addComponent(filterHintLabel);
		layout.addComponent(hListSelectLayout);
		layout.addComponent(hDateSelectLayout);
		layout.addComponent(hSearchLayout);
		layout.setSizeFull();
		
		Panel filterPanel = new Panel();
		filterPanel.setHeight(300, Unit.PIXELS);
		filterPanel.setWidth("100%");
		filterPanel.setContent(layout);
		filterPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		
		vSplitPanel.setFirstComponent(filterPanel);
		
		GridLayout hErrorTable = new GridLayout();
		hErrorTable.setWidth("100%");
		
		GridLayout buttons = new GridLayout(6, 1);
		buttons.setWidth("30px");
		
		
		
		Button jiraButton = new Button();
		jiraButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		jiraButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		jiraButton.setIcon(AtlassianIcons.JIRA);
		jiraButton.setImmediate(true);
		jiraButton.setDescription("Export JIRA table");
		
		jiraButton.addClickListener(new Button.ClickListener() 
        {
            public void buttonClick(ClickEvent event) 
            {	     
            	StringBuffer sb = new StringBuffer();
		    	
		    	for(Object property: container.getContainerPropertyIds())
		    	{
		    		if(container.getType(property) == String.class)
		    		{
		    			sb.append("||").append(property);
		    		}
		    	}
		    	sb.append("||\n");
		    	
		    	
		    	for(Object errorOccurrence: container.getItemIds())
		    	{
		    		Item item = container.getItem(errorOccurrence);
		    		
		    		
		    		for(Object propertyId: container.getContainerPropertyIds())
			    	{		    			
		    			if(container.getType(propertyId) == String.class)
			    		{
		    				Property property = item.getItemProperty(propertyId);
		    				
		    				sb.append("|").append(property.getValue());
			    		}
			    	}
		    		
		    		sb.append("|\n");
		    	}
		    	
		    	jiraClipboard = sb.toString();
            	
            	TextWindow tw = new TextWindow("Jira Table", jiraClipboard);
                
                UI.getCurrent().addWindow(tw);
            }
        });
		
		Button excelButton = new Button();
		excelButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		excelButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
		excelButton.setIcon(FontAwesome.FILE_EXCEL_O);
		excelButton.setImmediate(true);
		excelButton.setDescription("Export Excel table");
		
		FileDownloader fd = new FileDownloader(this.getExcelDownloadStream());
        fd.extend(excelButton);
		
		final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);
		
//		buttons.addComponent(jiraButton);
		buttons.addComponent(excelButton);
		
		HorizontalLayout hl = new HorizontalLayout();
		hl.setWidth("100%");
		hl.addComponent(buttons);
		hl.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);
		
		searchResultsSizeLayout.setWidth("100%");
		searchResultsSizeLayout.addComponent(this.resultsLabel);
		searchResultsSizeLayout.setComponentAlignment(this.resultsLabel, Alignment.MIDDLE_LEFT);
		
		GridLayout gl = new GridLayout(2, 1);
		gl.setWidth("100%");
		
		gl.addComponent(searchResultsSizeLayout);
		gl.addComponent(hl);
		
		hErrorTable.addComponent(gl);
		hErrorTable.addComponent(this.messageHistoryTable);
		
		vSplitPanel.setSecondComponent(hErrorTable);
		vSplitPanel.setSplitPosition(310, Unit.PIXELS);
		
		GridLayout wrapper = new GridLayout(1, 2);
		wrapper.setRowExpandRatio(0, .01f);
		wrapper.setRowExpandRatio(1, .99f);
		wrapper.setSizeFull();
		wrapper.addComponent(filterButtonLayout);
		wrapper.setComponentAlignment(filterButtonLayout, Alignment.MIDDLE_RIGHT);
		wrapper.addComponent(vSplitPanel);
		
		this.setSizeFull();
		this.addComponent(wrapper);
	}
	
	/**
     * Helper method to get the stream associated with the export of the file.
     * 
     * @return the StreamResource associated with the export.
     */
    private StreamResource getExcelDownloadStream() 
    {
		StreamResource.StreamSource source = new StreamResource.StreamSource() 
		{
		    public InputStream getStream() 
		    {
		    	ByteArrayOutputStream stream = null;
		    	
		        try
		        {
		            stream = getExcelStream();
		        }
		        catch (IOException e)
		        {
		        	logger.error(e.getMessage(), e);
		        }
		        
		        InputStream input = new ByteArrayInputStream(stream.toByteArray());
		        return input;
		    }
		};
            
	    StreamResource resource = new StreamResource ( source,"metrics.csv");
	    return resource;
    }
    
    /**
     * Helper method to get the ByteArrayOutputStream associated with the export.
     * 
     * @return
     * @throws IOException
     */
    private ByteArrayOutputStream getExcelStream() throws IOException
    {
    	ByteArrayOutputStream out = new ByteArrayOutputStream(); 
    	
    	StringBuffer sb = new StringBuffer();
    	sb.append("Module Name").append(",");
    	sb.append("Flow Name").append(",");
    	sb.append("Component Name").append(",");
    	sb.append("Life Identifier").append(",");
    	sb.append("Metric Name").append(",");
    	sb.append("Metric Result").append(",");
    	sb.append("Start Time").append(",");
    	sb.append("End Time").append(",");
    	sb.append("Payload").append("\r\n");
    	
    	List<String> eventIds = new ArrayList<String>();
    	
    	for(MessageHistoryEvent<String, CustomMetric> event: messageHistoryEvents.getPagedResults())
    	{
    		eventIds.add(event.getBeforeEventIdentifier());
    	}
    	
    	SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
    	Map<String, WiretapEvent> wiretaps = this.wiretapService.getWiretapEventsByLifeId(eventIds);
    		
    	for(MessageHistoryEvent<String, CustomMetric> event: messageHistoryEvents.getPagedResults())
    	{	
    		for(CustomMetric metric: event.getMetrics())
	    	{		
        	    String startTime = format.format(new Date(event.getStartTimeMillis()));
        	    String endTime = format.format(new Date(event.getEndTimeMillis()));
        	    
    			sb.append("\"").append(event.getModuleName()).append("\",");
    			sb.append("\"").append(event.getFlowName()).append("\",");
    			sb.append("\"").append(event.getComponentName()).append("\",");
    			sb.append("\"").append(event.getBeforeEventIdentifier()).append("\",");
    			sb.append("\"").append(metric.getName()).append("\",");
    			sb.append("\"").append(metric.getValue()).append("\",");
    			sb.append("\"").append(format.format(new Date(event.getStartTimeMillis()))).append("\",");
    			sb.append("\"").append(format.format(new Date(event.getEndTimeMillis()))).append("\",");
    			
    			WiretapEvent<String> wiretap = (WiretapEvent<String>)wiretaps.get(
    					event.getModuleName() + event.getFlowName() + "before " + event.getComponentName() + 
    					event.getBeforeEventIdentifier());
    			
    			if(wiretap == null)
    			{
    				wiretap = (WiretapEvent<String>)wiretaps.get(
        					event.getModuleName() + event.getFlowName() + "after " + event.getComponentName() + 
        					event.getBeforeEventIdentifier());
    			}
				
    			if(wiretap != null)
    			{
	    			String csvCell = wiretap.getEvent();
					if(csvCell != null && csvCell.contains("\""))
					{
						csvCell = csvCell.replaceAll("\"", "\"\"");
					}
					
					// Max length of a CSV cell in EXCEL
					if(csvCell != null && csvCell.length() > 32760)
					{
						csvCell = csvCell.substring(0, 32759);
					}
					
					sb.append("\"").append(csvCell).append("\"\r\n");
    			}
    			else
    			{
    				sb.append("\"").append("\"\r\n");
    				
	    		}
	    	}
    	}
    	
    	out.write(sb.toString().getBytes());
        
        return out;
    }
	
	protected void refreshTable(boolean showError, Collection<ErrorOccurrence> myItems)
	{
		messageHistoryTable.removeAllItems();
		
		container = buildContainer();
		this.messageHistoryTable.setContainerDataSource(container);

    	ArrayList<String> modulesNames = null;
    	
    	if(modules.getItemIds().size() > 0)
    	{
        	modulesNames = new ArrayList<String>();
        	for(Object module: modules.getItemIds())
        	{
        		modulesNames.add(((Module)module).getName());
        	}
    	}
    	
    	ArrayList<String> flowNames = null;
    	
    	if(flows.getItemIds().size() > 0)
    	{
    		flowNames = new ArrayList<String>();
    		for(Object flow: flows.getItemIds())
        	{
        		flowNames.add(((Flow)flow).getName());
        	}
    	}
    	
    	ArrayList<String> componentNames = null;
    	
    	if(components.getItemIds().size() > 0 
    			&& modules.getItemIds().size() == 0
    			&& flows.getItemIds().size() == 0)
    	{
    		componentNames = new ArrayList<String>();
        	for(Object component: components.getItemIds())
        	{
        		componentNames.add(((Component)component).getName());
        	}
    	}
    	
    	if(modulesNames == null && flowNames == null && componentNames == null && businessStreamCombo != null
    			&& !((BusinessStream)businessStreamCombo.getValue()).getName().equals("All"))
    	{
    		BusinessStream businessStream = ((BusinessStream)businessStreamCombo.getValue());
    		
    		modulesNames = new ArrayList<String>();
    		
    		for(BusinessStreamFlow flow: businessStream.getFlows())
    		{
    			modulesNames.add(flow.getFlow().getModule().getName());
    		}
    	}
    	
		
		messageHistoryEvents = this.messageHistoryService.findMessageHistoryEvents
				(0, platformConfigurationService.getSearchResultSetSize(), "startTimeMillis", false, modulesNames, flowNames, componentNames, 
						"", "", errorFromDate.getValue(), errorToDate.getValue(), true);
    	
    	if((messageHistoryEvents == null || messageHistoryEvents.getResultSize() == 0) && showError)
    	{
    		Notification.show("The metrics search returned no results!", Type.ERROR_MESSAGE);
    	}
    	
    	
    	searchResultsSizeLayout.removeAllComponents();
    	this.resultsLabel = new Label("Number of records returned: " + messageHistoryEvents.getPagedResults().size() + " of " + messageHistoryEvents.getResultSize());
    	searchResultsSizeLayout.addComponent(this.resultsLabel);
    	
    	if(messageHistoryEvents.getResultSize() > platformConfigurationService.getSearchResultSetSize())
    	{
    		Notification notif = new Notification(
    			    "Warning",
    			    "The number of results returned by this search exceeds the configured search " +
    			    "result size of " + platformConfigurationService.getSearchResultSetSize() + " records. " +
    			    "You can narrow the search with a filter or by being more accurate with the date and time range. ",
    			    Type.HUMANIZED_MESSAGE);
    		notif.setDelayMsec(-1);
    		notif.setStyleName(ValoTheme.NOTIFICATION_CLOSABLE);
    		notif.setPosition(Position.MIDDLE_CENTER);
    		
    		notif.show(Page.getCurrent());
    	}
    	
    	for(final MessageHistoryEvent<String, CustomMetric> messageHistoryEvent: messageHistoryEvents.getPagedResults())
    	{
    		SimpleDateFormat format = new SimpleDateFormat(DashboardConstants.DATE_FORMAT_TABLE_VIEWS);
    	    String startTime = format.format(new Date(messageHistoryEvent.getStartTimeMillis()));
    	    String endTime = format.format(new Date(messageHistoryEvent.getEndTimeMillis()));
    	    
    	    Item item = container.addItem(messageHistoryEvent);			            	    
			
			VerticalLayout layout = new VerticalLayout();
    	    layout.addComponent(new Label(VaadinIcons.ARCHIVE.getHtml() + " " +  messageHistoryEvent.getModuleName(), ContentMode.HTML));
    	    layout.addComponent(new Label(VaadinIcons.AUTOMATION.getHtml() + " " +  messageHistoryEvent.getFlowName(), ContentMode.HTML));
    	    layout.addComponent(new Label(VaadinIcons.COG.getHtml() + " " +  messageHistoryEvent.getComponentName(), ContentMode.HTML));
    	    layout.setSpacing(true);
    	                	    		            	    

    	    item.getItemProperty("Metric Location").setValue(layout);
	    	    
			item.getItemProperty("Event Id").setValue(messageHistoryEvent.getBeforeEventIdentifier());
			
			TextArea metricsTextArea = new TextArea();
			metricsTextArea.setRows(5);
			metricsTextArea.setWidth("300px");
			
			StringBuffer metrics = new StringBuffer();
			for(CustomMetric metric: messageHistoryEvent.getMetrics())
			{
				metrics.append(metric.getName() + " " + metric.getValue() + "\r\n");
			}
			
			metricsTextArea.setValue(metrics.toString());
			
			item.getItemProperty("Metrics").setValue(metricsTextArea);
			
			item.getItemProperty("Start Time").setValue(startTime);
			item.getItemProperty("End Time").setValue(endTime);
						
			
			Button popupButton = new Button();
			popupButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
			popupButton.setDescription("Open in new window");
			popupButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
			popupButton.setIcon(VaadinIcons.MODAL);

	        BrowserWindowOpener popupOpener = new BrowserWindowOpener(ErrorOccurrencePopup.class);
	        popupOpener.setFeatures("height=600,width=900,resizable");
	        popupOpener.extend(popupButton);
	        
	        popupButton.addClickListener(new Button.ClickListener() 
	    	{
	            public void buttonClick(ClickEvent event) 
	            {
//	            	VaadinService.getCurrentRequest().getWrappedSession().setAttribute("errorReportService", errorReportingService);
//	    	        VaadinService.getCurrentRequest().getWrappedSession().setAttribute("errorReportManagementService", errorReportingManagementService);
//	    	        VaadinService.getCurrentRequest().getWrappedSession().setAttribute("platformConfigurationService", platformConfigurationService);
//	    	        VaadinService.getCurrentRequest().getWrappedSession().setAttribute("errorOccurrence", messageHistoryEvent);
	            }
	        });
	        
	        item.getItemProperty(" ").setValue(popupButton);   
    	}
    
	}

	/* (non-Javadoc)
	 * @see org.ikasan.dashboard.ui.topology.component.TopologyTab#search()
	 */
	@Override
	public void search()
	{
		// TODO Auto-generated method stub
		
	}
}
