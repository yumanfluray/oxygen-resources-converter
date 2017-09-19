package com.oxygenxml.html.convertor.plugin;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.oxygenxml.html.convertor.translator.OxygenTranslator;
import com.oxygenxml.html.convertor.translator.Tags;
import com.oxygenxml.html.convertor.translator.Translator;
import com.oxygenxml.html.convertor.view.ConvertorDialog;

import ro.sync.ecss.extensions.api.AuthorAccess;
import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ro.sync.exml.workspace.api.listeners.WSEditorChangeListener;
import ro.sync.exml.workspace.api.standalone.MenuBarCustomizer;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ToolbarComponentsCustomizer;
import ro.sync.exml.workspace.api.standalone.ToolbarInfo;
import ro.sync.exml.workspace.api.standalone.ViewComponentCustomizer;
import ro.sync.exml.workspace.api.standalone.ViewInfo;
import ro.sync.exml.workspace.api.standalone.actions.MenusAndToolbarsContributorCustomizer;
import ro.sync.exml.workspace.api.standalone.ui.ToolbarButton;

/**
 * Plugin extension - workspace access extension.
 */
public class CustomWorkspaceAccessPluginExtension implements WorkspaceAccessPluginExtension {

	private final static String MENU_NAME = "Tools";
	private final static String ANTERIOR_MENU_ITEM_ACTION_NAME = "XML_to_JSON";
	private Translator translator = new OxygenTranslator();
	
	/**
	 * The custom messages area. A sample component added to your custom view.
	 */
	private JTextArea customMessagesArea;

	/**
	 * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#applicationStarted(ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace)
	 */
	@Override
	public void applicationStarted(final StandalonePluginWorkspace pluginWorkspaceAccess) {
		// You can set or read global options.
		// The "ro.sync.exml.options.APIAccessibleOptionTags" contains all
		// accessible keys.
		// pluginWorkspaceAccess.setGlobalObjectProperty("can.edit.read.only.files",
		// Boolean.FALSE);
		// Check In action

		// You can access the content inside each opened WSEditor depending on the
		// current editing page (Text/Grid or Author).
		// A sample action which will be mounted on the main menu, toolbar and
		// contextual menu.
		final Action selectionSourceAction = createShowSelectionAction(pluginWorkspaceAccess);
		// Mount the action on the contextual menus for the Text and Author modes.
		pluginWorkspaceAccess.addMenusAndToolbarsContributorCustomizer(new MenusAndToolbarsContributorCustomizer() {
			/**
			 * Customize the author popup menu.
			 */
			@Override
			public void customizeAuthorPopUpMenu(JPopupMenu popup, AuthorAccess authorAccess) {
				// Add our custom action
				popup.add(selectionSourceAction);
			}

			@Override
			public void customizeTextPopUpMenu(JPopupMenu popup, WSTextEditorPage textPage) {
				// Add our custom action
				popup.add(selectionSourceAction);
			}
		});

		// Create your own main menu and add it to Oxygen or remove one of Oxygen's
		// menus...
		pluginWorkspaceAccess.addMenuBarCustomizer(new MenuBarCustomizer() {
			/**
			 * @see ro.sync.exml.workspace.api.standalone.MenuBarCustomizer#customizeMainMenu(javax.swing.JMenuBar)
			 */
			@Override
			public void customizeMainMenu(JMenuBar mainMenuBar) {
				addActionInMenuBar(mainMenuBar, selectionSourceAction, pluginWorkspaceAccess);
			}
		});

		
	//add a item with checkerDocBook action in contextual menu of ProjectManager
			ProjectManagerEditor.addPopUpMenuCustomizer(pluginWorkspaceAccess, selectionSourceAction);
		
		
		pluginWorkspaceAccess.addEditorChangeListener(new WSEditorChangeListener() {
			@Override
			public boolean editorAboutToBeOpenedVeto(URL editorLocation) {
				// You can reject here the opening of an URL if you want
				return true;
			}

			@Override
			public void editorOpened(URL editorLocation) {
				checkActionsStatus(editorLocation);
			}

			// Check actions status
			private void checkActionsStatus(URL editorLocation) {
				WSEditor editorAccess = pluginWorkspaceAccess
						.getCurrentEditorAccess(StandalonePluginWorkspace.MAIN_EDITING_AREA);
				if (editorAccess != null) {
					selectionSourceAction.setEnabled(EditorPageConstants.PAGE_AUTHOR.equals(editorAccess.getCurrentPageID())
							|| EditorPageConstants.PAGE_TEXT.equals(editorAccess.getCurrentPageID()));
				}
			}

			@Override
			public void editorClosed(URL editorLocation) {
				// An edited XML document has been closed.
			}

			/**
			 * @see ro.sync.exml.workspace.api.listeners.WSEditorChangeListener#editorAboutToBeClosed(java.net.URL)
			 */
			@Override
			public boolean editorAboutToBeClosed(URL editorLocation) {
				// You can veto the closing of an XML document.
				// Allow close
				return true;
			}

			/**
			 * The editor was relocated (Save as was called).
			 * 
			 * @see ro.sync.exml.workspace.api.listeners.WSEditorChangeListener#editorRelocated(java.net.URL,
			 *      java.net.URL)
			 */
			@Override
			public void editorRelocated(URL previousEditorLocation, URL newEditorLocation) {
				//
			}

			@Override
			public void editorPageChanged(URL editorLocation) {
				checkActionsStatus(editorLocation);
			}

			@Override
			public void editorSelected(URL editorLocation) {
				checkActionsStatus(editorLocation);
			}

			@Override
			public void editorActivated(URL editorLocation) {
				checkActionsStatus(editorLocation);
			}
		}, StandalonePluginWorkspace.MAIN_EDITING_AREA);

		// You can use this callback to populate your custom toolbar (defined in the
		// plugin.xml) or to modify an existing Oxygen toolbar
		// (add components to it or remove them)
		pluginWorkspaceAccess.addToolbarComponentsCustomizer(new ToolbarComponentsCustomizer() {
			/**
			 * @see ro.sync.exml.workspace.api.standalone.ToolbarComponentsCustomizer#customizeToolbar(ro.sync.exml.workspace.api.standalone.ToolbarInfo)
			 */
			public void customizeToolbar(ToolbarInfo toolbarInfo) {
				// The toolbar ID is defined in the "plugin.xml"
				if ("SampleWorkspaceAccessToolbarID".equals(toolbarInfo.getToolbarID())) {
					List<JComponent> comps = new ArrayList<JComponent>();
					JComponent[] initialComponents = toolbarInfo.getComponents();
					boolean hasInitialComponents = initialComponents != null && initialComponents.length > 0;
					if (hasInitialComponents) {
						// Add initial toolbar components
						for (JComponent toolbarItem : initialComponents) {
							comps.add(toolbarItem);
						}
					}

					// Add your own toolbar button using our
					// "ro.sync.exml.workspace.api.standalone.ui.ToolbarButton" API
					// component
					ToolbarButton customButton = new ToolbarButton(selectionSourceAction, true);
					comps.add(customButton);
					toolbarInfo.setComponents(comps.toArray(new JComponent[0]));
				}
			}
		});

		pluginWorkspaceAccess.addViewComponentCustomizer(new ViewComponentCustomizer() {
			/**
			 * @see ro.sync.exml.workspace.api.standalone.ViewComponentCustomizer#customizeView(ro.sync.exml.workspace.api.standalone.ViewInfo)
			 */
			@Override
			public void customizeView(ViewInfo viewInfo) {
				if (
				// The view ID defined in the "plugin.xml"
				"SampleWorkspaceAccessID".equals(viewInfo.getViewID())) {
					customMessagesArea = new JTextArea("Messages:");
					viewInfo.setComponent(new JScrollPane(customMessagesArea));
					viewInfo.setTitle("Custom Messages");
					// You can have images located inside the JAR library and use them...
					// viewInfo.setIcon(new
					// ImageIcon(getClass().getClassLoader().getResource("images/customMessage.png").toString()));
				}
			}
		});
	}

	/**
	 * Create the Swing action which shows the current selection.
	 * 
	 * @param pluginWorkspaceAccess
	 *          The plugin workspace access.
	 * @return The "Show Selection" action
	 */
	@SuppressWarnings("serial")
	private AbstractAction createShowSelectionAction(final StandalonePluginWorkspace pluginWorkspaceAccess) {
		return new AbstractAction(translator.getTranslation(Tags.MENU_ITEM_TEXT)) {
			@Override
			public void actionPerformed(ActionEvent actionevent) {

				ConvertorDialog convertorDialog = new ConvertorDialog((JFrame) pluginWorkspaceAccess.getParentFrame(),
						translator);
			}
		};
	}

	/**
	 * @see ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension#applicationClosing()
	 */
	@Override
	public boolean applicationClosing() {
		// You can reject the application closing here
		return true;
	}

	
	/**
	 * Add the given Action in the given JMenuBar.
	 *
	 * @param mainMenuBar	 The menuBar
	 * @param actionToAdd	The action.
	 * @param pluginWorkspaceAccess	The oxygen PluginWorkspaceAccess.
	 */
	private void addActionInMenuBar(JMenuBar mainMenuBar, Action actionToAdd,
			StandalonePluginWorkspace pluginWorkspaceAccess) {

		// get the number of items in MenuBar
		int menuBarSize = mainMenuBar.getMenuCount();

		// iterate over items in menuBar
		for (int j = 0; j < menuBarSize; j++) {

			// get the menu with index j
			JMenu menu = mainMenuBar.getMenu(j);

			if (menu != null) {

				int sizeMenu = menu.getItemCount();
				for (int i = 0; i < sizeMenu; i++) {

					JMenuItem menuItem = menu.getItem(i);

					if (menuItem != null) {

						Action action = menuItem.getAction();
						if (action != null) {
							//get the actionID
							String actionID = pluginWorkspaceAccess.getOxygenActionID(action);

							//The actionId is in format: menuNameId/menuItemActionID 
							int indexOfSlash = actionID.indexOf("/");
							
							//check the menuNameID
							if (MENU_NAME.equals(actionID.substring(0, indexOfSlash))) {
									
								//the menuNameId is MENU_NAME
								//check the menuItemActionID
								if (ANTERIOR_MENU_ITEM_ACTION_NAME.equals(actionID.substring(indexOfSlash + 1))) {
									//the MenuIdemActionId is ANTERIOR_MENU_ITEM_ACTION_NAME.
									//add the action after this index.
									menu.add(new JMenuItem(actionToAdd), i + 1);
									
									//break the loops.
									j = menuBarSize;
									break;
								}
								
							} else {
								//the menuNameId is not MENU_NAME
								break;
							}
						}

					}
				}
			}

		}
	}
}