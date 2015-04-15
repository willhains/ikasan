/*
 * $Id: NavigationPanel.java 44073 2015-03-17 10:38:20Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/panel/NavigationPanel.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.framework.panel;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.action.LogoutAction;
import org.ikasan.dashboard.ui.framework.display.IkasanUIView;
import org.ikasan.dashboard.ui.framework.group.EditableGroup;
import org.ikasan.dashboard.ui.framework.group.FunctionalGroup;
import org.ikasan.dashboard.ui.framework.group.VisibilityGroup;
import org.ikasan.dashboard.ui.framework.navigation.IkasanUINavigator;
import org.ikasan.dashboard.ui.framework.util.UserDetailsHelper;
import org.ikasan.dashboard.ui.framework.window.IkasanMessageDialog;
import org.ikasan.dashboard.ui.framework.window.LoginDialog;
import org.ikasan.security.service.AuthenticationService;
import org.ikasan.security.service.UserService;

import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

/**
 * @author CMI2 Development Team
 * 
 */
public class NavigationPanel extends Panel
{

	private static final long serialVersionUID = 5649279357596506519L;

	/** Logger instance */
	private static Logger logger = Logger.getLogger(NavigationPanel.class);

	private UserService userService;
	private AuthenticationService authenticationService;
	private VisibilityGroup visibilityGroup;
	private UserDetailsHelper userDetailsHelper;
	private Button loginButton;
	private Button logoutButton;
	private EditableGroup editableGroup;
	private GridLayout layout = new GridLayout(4, 1);
	private FunctionalGroup newMappingConfigurationFunctionalGroup;
	private FunctionalGroup existingMappingConfigurationFunctionalGroup;
	private Button collapseButton;
	private Button expandButton;
	private VerticalLayout imagePanelLayout;
	private Label loggedInUserLabel;
	private HashMap<String, IkasanUINavigator> views;
	private String currentView;
	private String currentViewName;
	private String currentNavigator;
	private MenuBar actionMenu = new MenuBar();
	private MenuBar utilityMenu = new MenuBar();

	/**
	 * Constructor
	 * 
	 * @param userService
	 * @param authProvider
	 * @param visibilityGroup
	 * @param userDetailsHelper
	 * @param editableGroup
	 * @param newMappingConfigurationFunctionalGroup
	 * @param existingMappingConfigurationFunctionalGroup
	 */
	public NavigationPanel(UserService userService,
			AuthenticationService authenticationService,
			VisibilityGroup visibilityGroup,
			UserDetailsHelper userDetailsHelper, EditableGroup editableGroup,
			FunctionalGroup newMappingConfigurationFunctionalGroup,
			FunctionalGroup existingMappingConfigurationFunctionalGroup,
			VerticalLayout imagePanelLayout,
			HashMap<String, IkasanUINavigator> views)
	{
		this.userService = userService;
		this.authenticationService = authenticationService;
		this.visibilityGroup = visibilityGroup;
		this.userDetailsHelper = userDetailsHelper;
		this.editableGroup = editableGroup;
		this.newMappingConfigurationFunctionalGroup = newMappingConfigurationFunctionalGroup;
		this.existingMappingConfigurationFunctionalGroup = existingMappingConfigurationFunctionalGroup;
		this.imagePanelLayout = imagePanelLayout;
		this.views = views;
		init();
	}

	/**
	 * Helper method to initialise the object.
	 */
	protected void init()
	{
		logger.info("Initialising navigation panel.");

		this.setWidth(100, Unit.PERCENTAGE);
		this.setHeight(30, Unit.PIXELS);
		this.setStyleName("navigation");
		this.layout.setColumnExpandRatio(0, 45f);
		this.layout.setColumnExpandRatio(1, 50f);
		this.layout.setColumnExpandRatio(2, 2.5f);
		this.layout.setColumnExpandRatio(3, 2.5f);
		
		this.actionMenu.setStyleName("ikasan");
		this.utilityMenu.setStyleName("ikasan");
		
		this.createActionMenuItems();
		this.createUtilityMenuItems();

		this.layout.addComponent(actionMenu, 0, 0);
		this.layout.setComponentAlignment(actionMenu, Alignment.MIDDLE_LEFT);

		final LoginDialog dialog = new LoginDialog(userService,
				this.authenticationService, visibilityGroup, userDetailsHelper,
				this);

		this.loginButton = new Button("Login");
		this.loginButton.setStyleName(BaseTheme.BUTTON_LINK);
		this.loginButton.addStyleName("white");
		this.loginButton.addClickListener(new ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{

				UI.getCurrent().addWindow(dialog);
			}
		});
		this.layout.setWidth(97, Unit.PERCENTAGE);
		this.layout.setHeight(100, Unit.PERCENTAGE);
		this.layout.addComponent(this.loginButton, 2, 0);
		this.layout.setComponentAlignment(this.loginButton,
				Alignment.MIDDLE_RIGHT);


		logoutButton = new Button(new ThemeResource("images/user.png"));
		this.logoutButton.setStyleName(BaseTheme.BUTTON_LINK);
		this.logoutButton.addStyleName("white");
		this.logoutButton.addClickListener(new ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				manageLogout();
			}
		});

		this.collapseButton = new Button("^");
		this.collapseButton.setStyleName(BaseTheme.BUTTON_LINK);
		this.collapseButton.addStyleName("white");
		this.layout.addComponent(this.collapseButton, 3, 0);
		this.layout.setComponentAlignment(this.collapseButton,
				Alignment.MIDDLE_RIGHT);
		this.collapseButton.addClickListener(new ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				imagePanelLayout.setVisible(false);
				layout.removeComponent(collapseButton);
				layout.addComponent(expandButton, 3, 0);
				layout.setComponentAlignment(expandButton,
						Alignment.MIDDLE_RIGHT);
			}
		});

		this.expandButton = new Button("+");
		this.expandButton.setStyleName(BaseTheme.BUTTON_LINK);
		this.expandButton.addStyleName("white");
		this.expandButton.addClickListener(new ClickListener()
		{
			@Override
			public void buttonClick(ClickEvent event)
			{
				imagePanelLayout.setVisible(true);
				layout.removeComponent(expandButton);
				layout.addComponent(collapseButton, 3, 0);
				layout.setComponentAlignment(collapseButton,
						Alignment.MIDDLE_RIGHT);
			}
		});

		this.setContent(layout);
	}

	/**
	 * Helper method to create the action menu
	 */
	protected void createActionMenuItems()
	{
		MenuItem dashboards = actionMenu.addItem("Dashboards",
				new ThemeResource("images/menu-icon.png"), null);
		dashboards.setStyleName("ikasan");

		MenuBar.Command dashboardCommand = createNavigatorMenuCommand(
				"dashboard", "dashboardView");
		dashboards.addItem("Default", dashboardCommand);
		dashboards.addSeparator();
		dashboards.addItem("My custom dasboard 1", null, null);
		dashboards.addItem("My custom dasboard 2", null, null);

		MenuBar.Command mappingCommand = createNavigatorMenuCommand("mapping",
				"mappingView");
		MenuBar.Command errorCommand = createNavigatorMenuCommand("error",
				"errorView");
		MenuBar.Command replayCommand = createNavigatorMenuCommand("replay",
				"replayView");
		MenuBar.Command hospitalCommand = createNavigatorMenuCommand(
				"hospital", "hospitalView");
		MenuBar.Command topologyCommand = createNavigatorMenuCommand(
				"topology", "topologyView");

		// Another top-level item
		MenuItem service = this.actionMenu.addItem("Services",
				new ThemeResource("images/menu-icon.png"), null);
		service.setStyleName("ikasan");
		service.addItem("Topology", null, topologyCommand);
		service.addSeparator();
		service.addItem("Mapping", null, mappingCommand);
		service.addSeparator();
		service.addItem("Error", null, errorCommand);
		service.addItem("Replay", null, replayCommand);
		service.addItem("Hospital", null, hospitalCommand);
	}

	protected void createUtilityMenuItems()
	{
		MenuBar.Command helpCommand = new MenuBar.Command()
		{
			public void menuSelected(MenuItem selectedItem)
			{				
				JavaScript.getCurrent().execute
					("window.open('http://google.com', 'Help', 'height=300,width=200,resizable');");

			}
		};

		utilityMenu.addItem("", new ThemeResource(
				"images/help.png"), helpCommand);
		
		MenuBar.Command userCommand = createNavigatorMenuCommand("user",
				"userView");
		MenuBar.Command authenticationMethodCommand = createNavigatorMenuCommand("topLevel",
				"authenticationMethodView");
		MenuBar.Command principalManagementCommand = createNavigatorMenuCommand("principalManagement",
				"principalManagementView");
		MenuBar.Command roleManagementCommand = createNavigatorMenuCommand("roleManagement",
				"roleManagementView");
		MenuBar.Command policyManagementCommand = createNavigatorMenuCommand("policyManagement",
				"policyManagementView");

		MenuItem admin = utilityMenu.addItem("", new ThemeResource(
				"images/gear.png"), null);
		admin.setStyleName("ikasan");
		admin.addItem("Manage Users", null, userCommand);
		admin.addItem("Manage Principals", null, principalManagementCommand);
		admin.addItem("Manage Roles", null, roleManagementCommand);
		admin.addItem("Manage Policies", null, policyManagementCommand);
		admin.addItem("Security Administration", null, authenticationMethodCommand);		

		MenuBar.Command profileCommand = createNavigatorMenuCommand("profile",
				"profileView");
		MenuBar.Command logOutCommand = new MenuBar.Command()
		{
			public void menuSelected(MenuItem selectedItem)
			{
				manageLogout();
			}
		};

		MenuItem userItem = this.utilityMenu.addItem("", new ThemeResource(
				"images/user.png"), null);
		userItem.setStyleName("ikasan");
		userItem.addItem("Profile", profileCommand);
		userItem.addSeparator();
		userItem.addItem("Log Out", logOutCommand);
	}

	/**
	 * Method to manage a logout
	 */
	protected void manageLogout()
	{
		LogoutAction action = new LogoutAction(this.visibilityGroup,
				this.userDetailsHelper, this.editableGroup, this.layout,
				this.loginButton, this.utilityMenu, this.loggedInUserLabel);

		IkasanMessageDialog dialog = new IkasanMessageDialog("Logout",
				"You are about to log out. Any unsaved data will be lost. "
						+ "Are you sure you wish to proceed?.", action);

		UI.getCurrent().addWindow(dialog);
	}

	/**
	 * Method to set component state post commit.
	 * 
	 * @throws CommitException
	 */
	public void postCommit() throws CommitException
	{
		this.layout.removeComponent(this.loginButton);
		loggedInUserLabel = new Label("Logged in as "
				+ this.userDetailsHelper.getUserDetails().getUsername());
		loggedInUserLabel.setStyleName("ikasan-white");
		loggedInUserLabel.setVisible(false);
		this.layout.addComponent(loggedInUserLabel, 1, 0);
		this.layout.setComponentAlignment(loggedInUserLabel,
				Alignment.MIDDLE_RIGHT);
		this.layout.addComponent(this.utilityMenu, 2, 0);
		this.layout
				.setComponentAlignment(this.utilityMenu, Alignment.MIDDLE_RIGHT);

		this.newMappingConfigurationFunctionalGroup.initialiseButtonState();
		this.existingMappingConfigurationFunctionalGroup
				.initialiseButtonState();
	}

	private void loadTopLevelNavigator()
	{
		Navigator navigator = new Navigator(UI.getCurrent(), views.get(
				"topLevel").getContainer());

		for (IkasanUIView view : this.views.get("topLevel").getIkasanViews())
		{
			logger.info("Adding view:" + view.getPath());
			navigator.addView(view.getPath(), view.getView());
		}
	}

	private MenuBar.Command createNavigatorMenuCommand(
			final String navigatorName, final String viewName)
	{
		return new MenuBar.Command()
		{
			public void menuSelected(MenuItem selectedItem)
			{
				if (currentView == null
						|| !currentView.equals(views.get(navigatorName)
								.getName()))
				{
					loadTopLevelNavigator();

					UI.getCurrent().getNavigator().navigateTo(viewName);

					currentView = views.get(navigatorName).getName();
					currentNavigator = navigatorName;
					currentViewName = viewName;

					List<IkasanUIView> mappingViews = views.get(navigatorName)
							.getIkasanViews();

					Navigator navigator = new Navigator(UI.getCurrent(), views
							.get(navigatorName).getContainer());

					for (IkasanUIView view : mappingViews)
					{
						navigator.addView(view.getPath(), view.getView());
					}
				}
			}
		};
	}

	@Override
	public void setVisible(boolean visible)
	{
		this.layout.setVisible(visible);
	}

	public void resetCurrentView()
	{
		this.currentView = null;
	}
	
//	/**
//	 * 
//	 */
//	public void navigateToCurrentView()
//	{
//		logger.info("Loading to level navigator");
//		loadTopLevelNavigator();
//		
//		UI.getCurrent().getNavigator().navigateTo(this.currentViewName);
//		logger.info("Navigated to: " + this.currentViewName);
//		List<IkasanUIView> mappingViews = views.get(this.currentNavigator)
//				.getIkasanViews();
//		
//		Navigator navigator = new Navigator(UI.getCurrent(), views
//				.get(this.currentNavigator).getContainer());
//
//		for (IkasanUIView view : mappingViews)
//		{
//			navigator.addView(view.getPath(), view.getView());
//		}
//	}
//
//	/**
//	 * 
//	 * @return
//	 */
//	public boolean isCurrentViewNull()
//	{
//		return this.currentView == null;
//	}
}