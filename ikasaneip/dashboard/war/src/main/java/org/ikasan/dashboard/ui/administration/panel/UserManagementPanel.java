/*
 * $Id: EstateViewPanel.java 44073 2015-03-17 10:38:20Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/panel/EstateViewPanel.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.administration.panel;

import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Role;
import org.ikasan.security.model.User;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.SecurityServiceException;
import org.ikasan.security.service.UserService;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.event.dd.acceptcriteria.ClientSideCriterion;
import com.vaadin.event.dd.acceptcriteria.SourceIs;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.DragStartMode;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.TableDragMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.zybnet.autocomplete.server.AutocompleteField;
import com.zybnet.autocomplete.server.AutocompleteQueryListener;
import com.zybnet.autocomplete.server.AutocompleteSuggestionPickedListener;

/**
 * @author CMI2 Development Team
 * 
 */
public class UserManagementPanel extends Panel implements View
{
	private static final long serialVersionUID = 6005593259860222561L;

	private Logger logger = Logger.getLogger(UserManagementPanel.class);

	private UserService userService;
	private SecurityService securityService;
	private ComboBox rolesCombo;

	/**
	 * Constructor
	 * 
	 * @param ikasanModuleService
	 */
	public UserManagementPanel(UserService userService, SecurityService securityService)
	{
		super();
		this.userService = userService;
		if (this.userService == null)
		{
			throw new IllegalArgumentException("userService cannot be null!");
		}
		this.securityService = securityService;
		if (this.securityService == null)
		{
			throw new IllegalArgumentException(
					"securityService cannot be null!");
		}

		init();
	}

	@SuppressWarnings("deprecation")
	protected void init()
	{
		this.setWidth("100%");
		this.setHeight("100%");

		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(true);
		layout.setSizeFull();

		Panel securityAdministrationPanel = new Panel("User Management");
		securityAdministrationPanel.setStyleName("dashboard");
		securityAdministrationPanel.setHeight("100%");
		securityAdministrationPanel.setWidth("100%");

		GridLayout gridLayout = new GridLayout(3, 16);
		gridLayout.setWidth("100%");
		gridLayout.setHeight("100%");
		gridLayout.setMargin(true);
		gridLayout.setSizeFull();

		Label usernameLabel = new Label("Username");

		final AutocompleteField<User> usernameField = new AutocompleteField<User>();
		usernameField.setWidth("80%");

		final DragAndDropWrapper usernameFieldWrap = new DragAndDropWrapper(
				usernameField);
		usernameFieldWrap.setDragStartMode(DragStartMode.COMPONENT);
		usernameFieldWrap.setSizeUndefined();

		final AutocompleteField<User> firstName = new AutocompleteField<User>();
		firstName.setWidth("80%");
		final AutocompleteField<User> surname = new AutocompleteField<User>();
		surname.setWidth("80%");
		final TextField department = new TextField();
		department.setWidth("80%");
		final TextField email = new TextField();
		email.setWidth("80%");
		final Table roleTable = new Table();
		roleTable.addContainerProperty("Role", String.class, null);
		roleTable.setHeight("400px");
		roleTable.setWidth("200px");

		usernameField.setQueryListener(new AutocompleteQueryListener<User>()
		{
			@Override
			public void handleUserQuery(AutocompleteField<User> field,
					String query)
			{
				for (User user : userService.getUserByUsernameLike(query))
				{
					field.addSuggestion(user, user.getUsername());
				}
			}
		});

		usernameField.setSuggestionPickedListener(new AutocompleteSuggestionPickedListener<User>()
		{
			@Override
			public void onSuggestionPicked(User user)
			{
				firstName.setText(user.getFirstName());
				surname.setText(user.getSurname());
				department.setValue(user.getDepartment());
				email.setValue(user.getEmail());

				IkasanPrincipal principal;
				principal = securityService
						.findPrincipalByName(user.getUsername());

				roleTable.removeAllItems();

				for (Role role : principal.getRoles())
				{
					roleTable.addItem(new Object[]
					{ role.getName() }, role);
				}
			}
		});
		
		firstName.setQueryListener(new AutocompleteQueryListener<User>()
		{
			@Override
			public void handleUserQuery(AutocompleteField<User> field,
					String query)
			{
				for (User user : userService.getUserByFirstnameLike(query))
				{
					field.addSuggestion(user, user.getFirstName() + " " + user.getSurname());
				}
			}
		});

		firstName.setSuggestionPickedListener(new AutocompleteSuggestionPickedListener<User>()
		{
			@Override
			public void onSuggestionPicked(User user)
			{
				usernameField.setText(user.getUsername());
				firstName.setText(user.getFirstName());
				surname.setText(user.getSurname());
				department.setValue(user.getDepartment());
				email.setValue(user.getEmail());

				IkasanPrincipal principal;
				principal = securityService
						.findPrincipalByName(user.getUsername());

				roleTable.removeAllItems();

				for (Role role : principal.getRoles())
				{
					roleTable.addItem(new Object[]
					{ role.getName() }, role);
				}
			}
		});
		
		surname.setQueryListener(new AutocompleteQueryListener<User>()
		{
			@Override
			public void handleUserQuery(AutocompleteField<User> field,
					String query)
			{
				for (User user : userService.getUserBySurnameLike(query))
				{
					field.addSuggestion(user, user.getFirstName() + " " + user.getSurname());
				}
			}
		});

		surname.setSuggestionPickedListener(new AutocompleteSuggestionPickedListener<User>()
		{
			@Override
			public void onSuggestionPicked(User user)
			{
				usernameField.setText(user.getUsername());
				firstName.setText(user.getFirstName());
				surname.setText(user.getSurname());
				department.setValue(user.getDepartment());
				email.setValue(user.getEmail());

				IkasanPrincipal principal;
				principal = securityService
						.findPrincipalByName(user.getUsername());

				roleTable.removeAllItems();

				for (Role role : principal.getRoles())
				{
					roleTable.addItem(new Object[]
					{ role.getName() }, role);
				}
			}
		});

		gridLayout.addComponent(usernameLabel, 0, 0);
		gridLayout.addComponent(usernameFieldWrap, 1, 0);

		Label firstNameLabel = new Label("First name");
		gridLayout.addComponent(firstNameLabel, 0, 1);
		gridLayout.addComponent(firstName, 1, 1);

		Label surnameLabel = new Label("Surname");
		gridLayout.addComponent(surnameLabel, 0, 2);
		gridLayout.addComponent(surname, 1, 2);

		Label departmentLabel = new Label("Department");
		gridLayout.addComponent(departmentLabel, 0, 3);
		gridLayout.addComponent(department, 1, 3);

		Label emailLabel = new Label("Email address");
		gridLayout.addComponent(emailLabel, 0, 4);
		gridLayout.addComponent(email, 1, 4);
		
		gridLayout.addComponent(new Label("<hr />",ContentMode.HTML),0, 5, 1, 5);

		final ClientSideCriterion acceptCriterion = new SourceIs(usernameField);

		final Table dropTable = new Table();
		dropTable.addContainerProperty("Members", String.class, null);
//		dropTable.addContainerProperty("", Button.class, null);
		dropTable.setHeight("400px");
		dropTable.setWidth("200px");

		dropTable.setDragMode(TableDragMode.ROW);
		dropTable.setDropHandler(new DropHandler()
		{
			@Override
			public void drop(final DragAndDropEvent dropEvent)
			{
				// criteria verify that this is safe
				logger.info("Trying to drop: " + dropEvent);

				final WrapperTransferable t = (WrapperTransferable) dropEvent
						.getTransferable();

				final AutocompleteField sourceContainer = (AutocompleteField) t
						.getDraggedComponent();
				logger.info("sourceContainer.getText(): "
						+ sourceContainer.getText());

//				Button deleteButton = new Button();
//				ThemeResource deleteIcon = new ThemeResource(
//						"images/remove-icon.png");
//				deleteButton.setIcon(deleteIcon);
//				deleteButton.setStyleName(Reindeer.BUTTON_LINK);
				
				dropTable.addItem(new Object[]
						{ sourceContainer.getText()}, sourceContainer.getText());
				
				IkasanPrincipal principal = securityService.findPrincipalByName(sourceContainer.getText());
				principal.getRoles().add((Role)rolesCombo.getValue());
				
				securityService.savePrincipal(principal);

				roleTable.removeAllItems();
				
				for (Role role : principal.getRoles())
				{
					roleTable.addItem(new Object[]
					{ role.getName() }, role);
				}
			}

			@Override
			public AcceptCriterion getAcceptCriterion()
			{
				return AcceptAll.get();
			}
		});
		
		gridLayout.addComponent(roleTable, 0, 6, 1, 6);
					
		this.rolesCombo = new ComboBox("Groups");
		this.rolesCombo.addListener(new Property.ValueChangeListener() {
		    public void valueChange(ValueChangeEvent event) {
		        Role role = (Role)event.getProperty().getValue();
		        
		        logger.info("Value changed got Role: " + role);
		        
		        List<IkasanPrincipal> principals = securityService.getAllPrincipalsWithRole(role.getName());
				
				dropTable.removeAllItems();
				
				for(IkasanPrincipal principal: principals)
				{
					dropTable.addItem(new Object[]
							{ principal.getName() }, principal.getName());
				}
		    }
		});
			
		gridLayout.addComponent(this.rolesCombo, 2, 0);
		gridLayout.addComponent(dropTable, 2, 1, 2, 15);

		securityAdministrationPanel.setContent(gridLayout);
		layout.addComponent(securityAdministrationPanel);
		this.setContent(layout);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener
	 * .ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event)
	{
		List<Role> roles = this.securityService.getAllRoles();
		
		for(Role role: roles)
		{
			this.rolesCombo.addItem(role);
			this.rolesCombo.setItemCaption(role, role.getName());
		}
	}
}