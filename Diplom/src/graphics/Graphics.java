package graphics;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import logic.Admin;
import logic.Catalog;
import logic.CatalogTree;
import logic.Date;
import logic.Document;
import logic.Log;
import logic.Teacher;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class Graphics {
	private Display display;
	private Shell shell;
	private ToolBar toolBar;
	private Label numObjectsLabel;
	private Label diskSpaceLabel;
	private Tree tree;
	private Label treeScopeLabel;
	private Table table;
	private Label tableContentsOfLabel;
	private final String[] tableTitles = new String[] {"Имя","Дата","Дата утверждения","Версия","Тип","Автор"};
	private static final int[] tableWidths = new int[] { 150, 90, 90, 75, 75, 150 };
	private String currentCatalog = null;
	private String path = null;
	private MenuItem itemIn, itemOut;
	private ToolItem itemAddDoc, itemExportDoc, itemFolder, itemJournal;
	private Menu settingsMenu;
	private Admin administrator = new Admin();
	
	public static void main(String[] args) throws Exception {
	    Display display = new Display();
	    Graphics application = new Graphics();
	    Shell shell = application.open(display);    //Открыть главное окно
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch())
	        display.sleep();
	    }
	    display.dispose();
	}
	
	public Shell open(Display display) throws Exception {
	    // Создание окна	
		this.display = display;
	    shell = new Shell();
	    Menu bar = new Menu(shell, SWT.BAR);
        shell.setMenuBar(bar);
	    createShellContents();
	    shell.open();
	    getPath(); //Путь к папке, в которой хранятся документы
	    return shell;	    
	}
	//Создать Файл-меню
    private void createFileMenu(Menu parent) {
        Menu menu = new Menu(parent);
        MenuItem header = new MenuItem(parent, SWT.CASCADE);
        header.setText("Файл");
        header.setMenu(menu);

        itemIn = new MenuItem(menu, SWT.PUSH);
        itemIn.setText("Войти");
        itemIn.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		enterWindow();
        	}
        });
        
        itemOut = new MenuItem(menu, SWT.PUSH);
        itemOut.setText("Выйти");
        itemOut.setEnabled(false);
        itemOut.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		itemIn.setEnabled(true);
        		itemOut.setEnabled(false);
        		itemAddDoc.setEnabled(false);
        		itemExportDoc.setEnabled(false);
        		itemFolder.setEnabled(false);
        		itemJournal.setEnabled(false);
        		settingsMenu.setEnabled(false);
        	}
        });
        MenuItem item = new MenuItem(menu, SWT.SEPARATOR);
        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Поиск");
        item.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		openSearchWindow();
        	}
        });
        item = new MenuItem(menu, SWT.SEPARATOR);
        item = new MenuItem(menu, SWT.PUSH);
        item.setText("Закрыть");
        item.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		shell.close();
        	}
        });         
    }
    private void createSettingsMenu(Menu parent) {
    	settingsMenu = new Menu(parent);
    	settingsMenu.setEnabled(false);
        MenuItem header = new MenuItem(parent, SWT.CASCADE);
        header.setText("Настройки");
        header.setMenu(settingsMenu);

        MenuItem item = new MenuItem(settingsMenu, SWT.PUSH);
        item.setText("Преподаватели");
        item.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		teacherWindow();
        	}
        });
        
        item = new MenuItem(settingsMenu, SWT.SEPARATOR);
        item = new MenuItem(settingsMenu, SWT.PUSH);
        item.setText("Изменить логин/пароль");
        item.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		changeLoginPassword();
        	}
        });   
    }
    //Создаем содержимое формы
    private void createShellContents() throws Exception {
        shell.setText("Система учета кафедральной документации");
        Image image = new Image(display, "images/application.png");
        shell.setImage(image);
        Menu bar = new Menu(shell, SWT.BAR);
        shell.setMenuBar(bar);
        createFileMenu(bar);
        createSettingsMenu(bar);

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        gridLayout.marginHeight = gridLayout.marginWidth = 0;
        shell.setLayout(gridLayout);

        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gridData.horizontalSpan = 2;
        createToolBar(shell, gridData);             //Кнопочки

        SashForm sashForm = new SashForm(shell, SWT.NONE);
        sashForm.setOrientation(SWT.HORIZONTAL);
        gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
        gridData.horizontalSpan = 3;
        sashForm.setLayoutData(gridData);
        createTreeView(sashForm);              //Создать дерево
        createTableView(sashForm);             //Создать таблицу
        sashForm.setWeights(new int[] { 2, 5 });

        numObjectsLabel = new Label(shell, SWT.BORDER);
        gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
        gridData.widthHint = 185;
        numObjectsLabel.setLayoutData(gridData);

        diskSpaceLabel = new Label(shell, SWT.BORDER);
        gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
        gridData.horizontalSpan = 2;
        diskSpaceLabel.setLayoutData(gridData);
    }
    //Кнопочки
    private void createToolBar(final Shell shell, Object layoutData) {
        toolBar = new ToolBar(shell, SWT.NULL);
        toolBar.setLayoutData(layoutData);
        
        ToolItem item = new ToolItem(toolBar, SWT.PUSH);
        Image image = new Image(display, "images/viewmag.png");
        item.setImage(image);
        item.setToolTipText("Поиск");
        item.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		openSearchWindow();
        	}
        });

        item = new ToolItem(toolBar, SWT.PUSH);
        image = new Image(display, "images/document-list.png");
        item.setImage(image);
        item.setToolTipText("Список неутвержденных документов");
        item.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		table.removeAll(); 
        		ArrayList<Document> docList = Document.getDocumentsWithoutApproval();
        		fillTable(docList);
        	}
        });
        
        itemAddDoc = new ToolItem(toolBar, SWT.PUSH);
        image = new Image(display, "images/document-import.png");
        itemAddDoc.setImage(image);
        itemAddDoc.setToolTipText("Добавить документ");
        itemAddDoc.setEnabled(false);
        itemAddDoc.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		openImportWindow();
        	}
        });

        itemExportDoc = new ToolItem(toolBar, SWT.PUSH);
        image = new Image(display, "images/document-export.png");
        itemExportDoc.setImage(image);
        itemExportDoc.setToolTipText("Экспортировать документ");
        itemExportDoc.setEnabled(false);
        itemExportDoc.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		openExportWindow();
        	}
        });

        itemFolder = new ToolItem(toolBar, SWT.PUSH);
        image = new Image(display, "images/folder-new.png");
        itemFolder.setImage(image);
        itemFolder.setToolTipText("Добавить папку");
        itemFolder.setEnabled(false);
        itemFolder.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		addFolder();
        	}
        });
        itemJournal = new ToolItem(toolBar, SWT.PUSH);
        image = new Image(display, "images/journal.png");
        itemJournal.setImage(image);
        itemJournal.setToolTipText("Журнал");
        itemJournal.setEnabled(false);
        itemJournal.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		openLogWindow();
        	}
        });
    }
    //Войти
    private void enterWindow() {
    	final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    	Point pt = display.getCursorLocation();
    	dialog.setLocation(pt.x, pt.y);
    	dialog.setText("Авторизация");
    	
    	GridLayout gridLayout = new GridLayout(2, false);
		dialog.setLayout(gridLayout);
		
		GridData data = new GridData();
		data.widthHint = 150;
		
		Label loginLabel = new Label(dialog, SWT.NONE);
		loginLabel.setText("Введите логин");
		
		final Text loginText = new Text(dialog, SWT.BORDER);
		loginText.setLayoutData(data);
		
		Label passLabel = new Label(dialog, SWT.NONE);
		passLabel.setText("Введите пароль");
		
		final Text passText = new Text(dialog, SWT.BORDER | SWT.PASSWORD);
		passText.setLayoutData(data);
		
		Button ok = new Button(dialog, SWT.SINGLE | SWT.PUSH);
		ok.setText("Войти");
		ok.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, true, true));
		
		Button cancel = new Button(dialog, SWT.SINGLE | SWT.PUSH);
		cancel.setText("Отмена");
		
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				dialog.close();
			}
		});
		
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String login = loginText.getText();
				String pass = passText.getText();
				if(login.equals("") || pass.equals("")) {
					warningMessage("Заполните все поля!");
					loginText.setText("");
					passText.setText("");
				} else {
					Admin b = Admin.authorization(login, pass);
					if(b == null) {
						warningMessage("Неверный логин и/или пароль!");
						loginText.setText("");
						passText.setText("");
					} else {
						administrator.setLogin(b.getLogin());
						administrator.setPassword(b.getPassword());
						administrator.setName(b.getName());
						itemOut.setEnabled(true);
						itemIn.setEnabled(false);
		        		itemAddDoc.setEnabled(true);
		        		itemExportDoc.setEnabled(true);
		        		itemFolder.setEnabled(true);
		        		itemJournal.setEnabled(true);
		        		settingsMenu.setEnabled(true);
						dialog.close();
					}
				}
			}
		});
		
		dialog.pack();
		dialog.open();
    }
    //Добавить документ
    private void openImportWindow() {
    	FileDialog dialog = new FileDialog(shell, SWT.OPEN);
    	dialog.setText("Добавить документ");
    	String platform = SWT.getPlatform();
    	dialog.setFilterPath(platform.equals("win32") || platform.equals("wpf") ? "c:\\" : "/");
    	
    	String pathOldDoc = dialog.open();
    	if(pathOldDoc != null) {
    		File oldDoc = new File(pathOldDoc);
        	File newDoc = new File(path + File.separator + Catalog.getPath(currentCatalog) + oldDoc.getName());
        	
        	addAuthor(oldDoc, newDoc);
    	}
    }
  //Добавить документ
    private void importDocument(File oldDoc, File newDoc, int id) {
    	InputStream inStream = null;
    	OutputStream outStream = null;
    	try {
    		inStream = new FileInputStream(oldDoc);
    		outStream = new FileOutputStream(newDoc);
    		
    		byte[] buffer = new byte[1024];
    		int length;
    		
    		while((length = inStream.read(buffer)) > 0) {
    			outStream.write(buffer, 0, length);
    		}
    		inStream.close();
    		outStream.close();
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    	Date date = Date.getCurrentDate();
    	Document doc = new Document();
    	Log log = new Log();
    	
    	doc.setName(newDoc.getName().substring(0, newDoc.getName().lastIndexOf(".")));
    	doc.setType(newDoc.getName().substring(newDoc.getName().lastIndexOf(".")+1));
    	doc.setAddress(newDoc.getPath().substring(path.length()+1));  
    	System.out.println(doc.getAddress());
    	doc.setIdTeacher(id);
    	doc.setIdDocumentList(Catalog.getCatalogIdByName(currentCatalog));
    	doc.setDate(date);
    	int i = Document.addDocument(doc);
    	log.setDocument(doc.getName());
    	log.setDocType(doc.getType());
    	if(i == 0)
    		log.setAction("Добавлен");
    	if(i == 1)
    		log.setAction("Изменен");
    	log.setDate(date);
    	Log.addLog(log);
    	refresh();
    }
    //Добавить автора
    private void addAuthor(final File oldDoc, final File newDoc) {
    	final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    	Point pt = display.getCursorLocation();
    	dialog.setLocation(pt.x, pt.y);
    	dialog.setText("Добавить автора");
    	
    	GridLayout gridLayout = new GridLayout(2, false);
		dialog.setLayout(gridLayout);
		
		Label label = new Label(dialog, SWT.NONE);
		label.setText("Выберите автора");
		
		ArrayList<Teacher> teachers = Teacher.getTeachers();
		final Combo combo = new Combo(dialog, SWT.DROP_DOWN);
		combo.setText("-преподаватель-");
		for(int i = 0; i < teachers.size(); i++) 
			combo.add(teachers.get(i).getName());
		
		Button ok = new Button(dialog, SWT.PUSH);
		ok.setText("Готово");

		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(!combo.getText().equals("-преподаватель-")) {
					int id = Teacher.getTeacherId(combo.getText());
					importDocument(oldDoc, newDoc, id);
				} else {
					warningMessage("Выберите преподавателя!");
					addAuthor(oldDoc, newDoc);
				}
				dialog.close();
			}
		});
		dialog.pack();
		dialog.open();
    }
    //Извлечь документ
    private void openExportWindow(){
    	DirectoryDialog dialog = new DirectoryDialog(shell);
    	String platform = SWT.getPlatform();
    	dialog.setFilterPath(platform.equals("win32") || platform.equals("wpf") ? "c:\\" : "/");
    	dialog.setText("Экспортировать документ");
    	String docName = null;
    	String docType = null;
    	
    	TableItem[] items = table.getSelection();
    	if(items == null || items.length == 0) {
    		warningMessage("Выберите документ!");
    	} else {
			TableItem item = items[0];
			docName = item.getText();
			Document doc = Document.getDocumentByName(docName);
			docType = doc.getType();
		
			String pathNewDoc = dialog.open();
			if(pathNewDoc != null) {	
				File newDoc = new File(pathNewDoc + File.separator + docName + "." + docType);
				File oldDoc = new File(path + File.separator + Catalog.getPath(currentCatalog) + docName + "." + docType);
		
				exportDocument(newDoc, oldDoc);
			}
		}
    }
  //Извлечь документ
    private void exportDocument(File newDoc, File oldDoc) {
    	InputStream inStream = null;
    	OutputStream outStream = null;
    	try {
    		inStream = new FileInputStream(oldDoc);
    		outStream = new FileOutputStream(newDoc);
    		
    		byte[] buffer = new byte[1024];
    		int length;
    		
    		while((length = inStream.read(buffer)) > 0) {
    			outStream.write(buffer, 0, length);
    		}
    		inStream.close();
    		outStream.close();
    	} catch(IOException e) {
    		warningMessage(e.getMessage());
    	}
    }
    //Добавить папку
    private void addFolder() {
    	final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    	Point pt = display.getCursorLocation();
    	dialog.setLocation(pt.x, pt.y);
    	dialog.setText("Добавить папку");
		FormLayout form = new FormLayout();
		form.marginWidth = 10;
		form.marginHeight = 10;
		form.spacing = 10;
		dialog.setLayout(form);
		
		Label label = new Label(dialog, SWT.CENTER);
		label.setText("Введите название папки");
		FormData data = new FormData();
		label.setLayoutData(data);
		
		Button cancel = new Button(dialog, SWT.PUSH);
		cancel.setText("Отмена");
		data = new FormData();
		data.width = 60;
		data.right = new FormAttachment(100, 0);
		data.bottom = new FormAttachment(100, 0);
		cancel.setLayoutData(data);
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				dialog.close();
			}
		});
		
		final Text text = new Text(dialog, SWT.LEFT | SWT.BORDER);
		data = new FormData();
		data.width = 200;
		data.top = new FormAttachment(label, 0, SWT.DEFAULT);
		data.bottom = new FormAttachment(cancel, 0, SWT.DEFAULT);
		text.setLayoutData(data);
		
		Button ok = new Button(dialog, SWT.PUSH);
		ok.setText("Готово");
		data = new FormData();
		data.width = 60;
		data.right = new FormAttachment(cancel, 0, SWT.DEFAULT);
		data.bottom = new FormAttachment(100, 0);
		ok.setLayoutData(data);
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String folderName = text.getText();
				if(folderName == "") {
					warningMessage("Введите название папки!");
					addFolder();
				}				
				if(folderName!="" && currentCatalog!=null) {
					int i = Catalog.addCatalog(folderName, currentCatalog);
					if(i == 0) {
						File file = new File(path + File.separator + Catalog.getPath(currentCatalog) + folderName);
						file.mkdir();
					}
					refresh();
				}
				dialog.close();
			}
		});		
		dialog.pack();
		dialog.open();
    }
    //Поиск
    private void openSearchWindow() {
    	final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    	Point pt = display.getCursorLocation();
    	dialog.setLocation(pt.x, pt.y);
    	dialog.setText("Поиск");
    	Integer[] days = new Integer [31];
    	String[] monthes = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
    	Integer[] years = new Integer [20];
    	
    	for(Integer i = 0; i < days.length; i++)
    		days[i] = new Integer (i+1);
    	for(Integer i = 0; i < years.length; i++) {
    		Calendar cal = Calendar.getInstance();
    		years[i] = new Integer (cal.get(Calendar.YEAR) - i);
    	}	
    	
    	GridLayout gridLayout = new GridLayout(2, false);
		dialog.setLayout(gridLayout);
				
		final Button btnName = new Button(dialog, SWT.LEFT | SWT.CHECK);
		btnName.setText("Введите название документа");
		
		GridData data = new GridData();
		data.widthHint = 200;
		data.heightHint = 17;
		final Text docName = new Text(dialog, SWT.SINGLE | SWT.BORDER);
		docName.setLayoutData(data);
		docName.setVisible(false);
		
		final Button btnTeacher = new Button(dialog, SWT.LEFT | SWT.CHECK);
		btnTeacher.setText("Выберите преподавателя");
		
		final Combo comboTeacher = new Combo(dialog, SWT.DROP_DOWN);
		comboTeacher.setText("-преподаватель-");
		ArrayList<Teacher> teachers = Teacher.getTeachers();
		for(int i = 0; i < teachers.size(); i++) 
			comboTeacher.add(teachers.get(i).getName());
		comboTeacher.setLayoutData(data);
		comboTeacher.setVisible(false);
		
		final Button btnDate = new Button(dialog, SWT.LEFT | SWT.CHECK);
		btnDate.setText("Введите дату");
		
		final Group gr = new Group(dialog, SWT.NONE);
		gr.setLayout(new RowLayout(SWT.HORIZONTAL));
		gr.setVisible(false);
		
		final Combo comboYear = new Combo(gr, SWT.DROP_DOWN);
		comboYear.setText("-год-");
		for(int i = 0; i < years.length; i++) 
			comboYear.add(years[i].toString());
		
		final Combo comboMonth = new Combo(gr, SWT.DROP_DOWN);
		comboMonth.setText("-месяц-");
		for(int i = 0; i < monthes.length; i++) 
			comboMonth.add(monthes[i]);
		comboMonth.setVisible(false);
		
		final Combo comboDay = new Combo(gr, SWT.DROP_DOWN);
		comboDay.setText("-день-");
		for(int i = 0; i < days.length; i++) 
			comboDay.add(days[i].toString());
		comboDay.setVisible(false);
		
		final Button btnAppDate = new Button(dialog, SWT.LEFT | SWT.CHECK);
		btnAppDate.setText("Введите дату утверждения");
		
		final Group gr2 = new Group(dialog, SWT.NONE);
		gr2.setLayout(new RowLayout(SWT.HORIZONTAL));
		gr2.setVisible(false);
		
		final Combo comboAppYear = new Combo(gr2, SWT.DROP_DOWN);
		comboAppYear.setText("-год-");
		for(int i = 0; i < years.length; i++) 
			comboAppYear.add(years[i].toString());
		
		final Combo comboAppMonth = new Combo(gr2, SWT.DROP_DOWN);
		comboAppMonth.setText("-месяц-");
		for(int i = 0; i < monthes.length; i++) 
			comboAppMonth.add(monthes[i]);
		comboAppMonth.setVisible(false);
		
		final Combo comboAppDay = new Combo(gr2, SWT.DROP_DOWN);
		comboAppDay.setText("-день-");
		for(int i = 0; i < days.length; i++) 
			comboAppDay.add(days[i].toString());
		comboAppDay.setVisible(false);

		Button ok = new Button(dialog, SWT.SINGLE | SWT.PUSH);
		ok.setText("Готово");
		ok.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, true, true));
		
		Button cancel = new Button(dialog, SWT.SINGLE | SWT.PUSH);
		cancel.setText("Отмена");
		
		btnName.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(btnName.getSelection()) {
					docName.setVisible(true);
				} else {
					docName.setVisible(false);
				}
			}
		});
		btnDate.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(btnDate.getSelection()) {
					gr.setVisible(true);
				} else {
					gr.setVisible(false);
				}
			}
		});
		btnAppDate.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(btnAppDate.getSelection()) {
					gr2.setVisible(true);
				} else {
					gr2.setVisible(false);
				}
			}
		});
		btnTeacher.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(btnTeacher.getSelection()) {
					comboTeacher.setVisible(true);
				} else {
					comboTeacher.setVisible(false);
				}
			}
		});
		comboYear.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(!(comboYear.getText() == "-год-")) {
					comboMonth.setVisible(true);
				} else {
					comboMonth.setVisible(false);
				}
			}
		});
		comboMonth.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(!(comboMonth.getText() == "-месяц-")) {
					comboDay.setVisible(true);
				} else {
					comboDay.setVisible(false);
				}
			}
		});
		comboAppYear.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(!(comboAppYear.getText() == "-год-")) {
					comboAppMonth.setVisible(true);
				} else {
					comboAppMonth.setVisible(false);
				}
			}
		});
		comboAppMonth.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(!(comboAppMonth.getText() == "-месяц-")) {
					comboAppDay.setVisible(true);
				} else {
					comboAppDay.setVisible(false);
				}
			}
		});
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				dialog.close();
			}
		});
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String name = docName.getText();
				String day = comboDay.getText();
				String month = comboMonth.getText();
				String year = comboYear.getText();
				String teach = comboTeacher.getText();
				String appDay = comboAppDay.getText();
				String appMonth = comboAppMonth.getText();
				String appYear = comboAppYear.getText();
				//Поиск только по названию документа
				if(btnName.getSelection() && !btnDate.getSelection() && !btnTeacher.getSelection() && !btnAppDate.getSelection()) {					
					if(name.equals("")){
						warningMessage("Введите название документа!");
						openSearchWindow();
					} else {
						searchDocument(name, null, null, 0, 1);
					}
				}
				//Поиск только по дате
				if(!btnName.getSelection() && btnDate.getSelection() && !btnTeacher.getSelection() && !btnAppDate.getSelection()) {
					if(year.equals("-год-")) {
						warningMessage("Выберите год!");
						openSearchWindow();
					} else if(month.equals("-месяц-")) {
						Date date = new Date(null, null, Integer.parseInt(year));
						searchDocument(null, date, null, 0, 2);	
					} else if(day.equals("-день-")) {
						Date date = new Date(null, month, Integer.parseInt(year));
						searchDocument(null, date, null, 0, 2);
					} else {
						Date date = new Date(Integer.parseInt(day), month, Integer.parseInt(year));
						searchDocument(null, date, null, 0, 2);
					}
				}
				//Поиск только по преподавателю
				if(!btnName.getSelection() && !btnDate.getSelection() && btnTeacher.getSelection() && !btnAppDate.getSelection()) {
					if(teach.equals("-преподаватель-")) {
						warningMessage("Выберите преподавателя!");
						openSearchWindow();
					} else {
						int teachId = Teacher.getTeacherId(teach);
						searchDocument(null, null, null, teachId, 3);
					}
				}
				//Поиск по дате утверждения
				if(!btnName.getSelection() && !btnDate.getSelection() && !btnTeacher.getSelection() && btnAppDate.getSelection()) {
					if(appYear.equals("-год-")) {
						warningMessage("Выберите год!");
						openSearchWindow();
					} else if(appMonth.equals("-месяц-")) {
						Date date = new Date(null, null, Integer.parseInt(appYear));
						searchDocument(null, null, date, 0, 4);	
					} else if(appDay.equals("-день-")) {
						Date date = new Date(null, appMonth, Integer.parseInt(appYear));
						searchDocument(null, null, date, 0, 4);
					} else {
						Date date = new Date(Integer.parseInt(appDay), appMonth, Integer.parseInt(appYear));
						searchDocument(null, null, date, 0, 4);
					}
				}
				//Поиск по названию документа и по дате
				if(btnName.getSelection() && btnDate.getSelection() && !btnTeacher.getSelection() && !btnAppDate.getSelection()) {
					if(name.equals("") || year.equals("-год-")) {
						warningMessage("Заполните все поля!");
						openSearchWindow();
					} else if(month.equals("-месяц-")) {
						Date date = new Date(null, null, Integer.parseInt(year));
						searchDocument(name, date, null, 0, 5);	
					} else if(day.equals("-день-")) {
						Date date = new Date(null, month, Integer.parseInt(year));
						searchDocument(name, date, null, 0, 5);
					} else {
						Date date = new Date(Integer.parseInt(day), month, Integer.parseInt(year));
						searchDocument(name, date, null, 0, 5);
					}
				}
				//Поиск по названию документа и преподавателю
				if(btnName.getSelection() && !btnDate.getSelection() && btnTeacher.getSelection() && !btnAppDate.getSelection()) {
					if(name.equals("") || teach.equals("-преподаватель-")) {
						warningMessage("Заполните все поля!");
						openSearchWindow();
					} else {
						int teachId = Teacher.getTeacherId(teach);
						searchDocument(name, null, null, teachId, 6);
					}
				}
				//Поиск по названию документа и по дате утверждения
				if(btnName.getSelection() && !btnDate.getSelection() && !btnTeacher.getSelection() && btnAppDate.getSelection()) {					
					if(name.equals("") || appYear.equals("-год-")) {
						warningMessage("Заполните все поля!");
						openSearchWindow();
					} else if(appMonth.equals("-месяц-")) {
						Date date = new Date(null, null, Integer.parseInt(appYear));
						searchDocument(name, null, date, 0, 7);	
					} else if(appDay.equals("-день-")) {
						Date date = new Date(null, appMonth, Integer.parseInt(appYear));
						searchDocument(name, null, date, 0, 7);
					} else {
						Date date = new Date(Integer.parseInt(appDay), appMonth, Integer.parseInt(appYear));
						searchDocument(name, null, date, 0, 7);
					}
				}
				//Поиск по дате и преподавателю
				if(!btnName.getSelection() && btnDate.getSelection() && btnTeacher.getSelection() && !btnAppDate.getSelection()) {
					if( teach.equals("-преподаватель-") || year.equals("-год-")) {
						warningMessage("Заполните все поля!");
						openSearchWindow();
					} else if(month.equals("-месяц-")) {
						Date date = new Date(null, null, Integer.parseInt(year));
						int teachId = Teacher.getTeacherId(teach);
						searchDocument(null, date, null, teachId, 8);	
					} else if(day.equals("-день-")) {
						Date date = new Date(null, month, Integer.parseInt(year));
						int teachId = Teacher.getTeacherId(teach);
						searchDocument(null, date, null, teachId, 8);
					} else {
						Date date = new Date(Integer.parseInt(day), month, Integer.parseInt(year));
						int teachId = Teacher.getTeacherId(teach);
						searchDocument(null, date, null, teachId, 8);
					}
				}
				//Поиск по дате и по дате утверждения
				if(!btnName.getSelection() && btnDate.getSelection() && !btnTeacher.getSelection() && btnAppDate.getSelection()) {
					if(appYear.equals("-год-") || year.equals("-год-")) {
						warningMessage("Заполните все поля!");
						openSearchWindow();
					} else if(month.equals("-месяц-") && appMonth.equals("-месяц-")) {
						Date date = new Date(null, null, Integer.parseInt(year));
						Date appDate = new Date(null, null, Integer.parseInt(appYear));
						searchDocument(null, date, appDate, 0, 9);	
					} else if(month.equals("-месяц-") && appDay.equals("-день-")) {
						Date date = new Date(null, null, Integer.parseInt(year));
						Date appDate = new Date(null, appMonth, Integer.parseInt(appYear));
						searchDocument(null, date, appDate, 0, 9);
					} else if(month.equals("-месяц-") && !appDay.equals("-день-")) {
						Date date = new Date(null, null, Integer.parseInt(year));
						Date appDate = new Date(Integer.parseInt(appDay), appMonth, Integer.parseInt(appYear));
						searchDocument(null, date, appDate, 0, 9);
					} else if(appMonth.equals("-месяц-") && day.equals("-день-")) {
						Date date = new Date(null, month, Integer.parseInt(year));
						Date appDate = new Date(null, null, Integer.parseInt(appYear));
						searchDocument(null, date, appDate, 0, 9);
					} else if(appMonth.equals("-месяц-") && !day.equals("-день-")) {
						Date date = new Date(Integer.parseInt(day), month, Integer.parseInt(year));
						Date appDate = new Date(null, null, Integer.parseInt(appYear));
						searchDocument(null, date, appDate, 0, 9);
					} else if(day.equals("-день-") && appDay.equals("-день-")) {
						Date date = new Date(null, month, Integer.parseInt(year));
						Date appDate = new Date(null, appMonth, Integer.parseInt(appYear));
						searchDocument(null, date, appDate, 0, 9);
					} else if(day.equals("-день-") && !appDay.equals("-день-")) {
						Date date = new Date(null, month, Integer.parseInt(year));
						Date appDate = new Date(Integer.parseInt(appDay), appMonth, Integer.parseInt(appYear));
						searchDocument(null, date, appDate, 0, 9);
					} else if(!day.equals("-день-") && appDay.equals("-день-")) {
						Date date = new Date(Integer.parseInt(day), month, Integer.parseInt(year));
						Date appDate = new Date(null, appMonth, Integer.parseInt(appYear));
						searchDocument(null, date, appDate, 0, 9);
					} else {
						Date date = new Date(Integer.parseInt(day), month, Integer.parseInt(year));
						Date appDate = new Date(Integer.parseInt(appDay), appMonth, Integer.parseInt(appYear));
						searchDocument(null, date, appDate, 0, 9);
					}
				}
				//Поиск по дате утверждения и преподавателю
				if(!btnName.getSelection() && !btnDate.getSelection() && btnTeacher.getSelection() && btnAppDate.getSelection()) {
					if(teach.equals("-преподаватель-") || appYear.equals("-год-")) {
						warningMessage("Заполните все поля!");
						openSearchWindow();
					} else if(appMonth.equals("-месяц-")) {
						Date date = new Date(null, null, Integer.parseInt(appYear));
						int teachId = Teacher.getTeacherId(teach);
						searchDocument(null, null, date, teachId, 10);	
					} else if(appDay.equals("-день-")) {
						Date date = new Date(null, appMonth, Integer.parseInt(appYear));
						int teachId = Teacher.getTeacherId(teach);
						searchDocument(null, null, date, teachId, 10);
					} else {
						Date date = new Date(Integer.parseInt(appDay), appMonth, Integer.parseInt(appYear));
						int teachId = Teacher.getTeacherId(teach);
						searchDocument(null, null, date, teachId, 10);
					}
				}
				//Поиск по названию, дате и преподавателю
				if(btnName.getSelection() && btnDate.getSelection() && btnTeacher.getSelection() && !btnAppDate.getSelection()) {
					if(name.equals("") || teach.equals("-преподаватель-") || year.equals("-год-")) {
						warningMessage("Заполните все поля!");
						openSearchWindow();
					} else if(month.equals("-месяц-")) {
						Date date = new Date(null, null, Integer.parseInt(year));
						int teachId = Teacher.getTeacherId(teach);
						searchDocument(name, date, null, teachId, 11);	
					} else if(day.equals("-день-")) {
						Date date = new Date(null, month, Integer.parseInt(year));
						int teachId = Teacher.getTeacherId(teach);
						searchDocument(name, date, null, teachId, 11);
					} else {
						Date date = new Date(Integer.parseInt(day), month, Integer.parseInt(year));
						int teachId = Teacher.getTeacherId(teach);
						searchDocument(name, date, null, teachId, 11);
					}
				}
				//Поиск по названию, дате и дате утверждения
				if(btnName.getSelection() && btnDate.getSelection() && !btnTeacher.getSelection() && btnAppDate.getSelection()) {
					if(name.equals("") || appYear.equals("-год-") || year.equals("-год-")) {
						warningMessage("Заполните все поля!");
						openSearchWindow();
					} else if(month.equals("-месяц-") && appMonth.equals("-месяц-")) {
						Date date = new Date(null, null, Integer.parseInt(year));
						Date appDate = new Date(null, null, Integer.parseInt(appYear));
						searchDocument(name, date, appDate, 0, 12);	
					} else if(month.equals("-месяц-") && appDay.equals("-день-")) {
						Date date = new Date(null, null, Integer.parseInt(year));
						Date appDate = new Date(null, appMonth, Integer.parseInt(appYear));
						searchDocument(name, date, appDate, 0, 12);
					} else if(month.equals("-месяц-") && !appDay.equals("-день-")) {
						Date date = new Date(null, null, Integer.parseInt(year));
						Date appDate = new Date(Integer.parseInt(appDay), appMonth, Integer.parseInt(appYear));
						searchDocument(name, date, appDate, 0, 12);
					} else if(appMonth.equals("-месяц-") && day.equals("-день-")) {
						Date date = new Date(null, month, Integer.parseInt(year));
						Date appDate = new Date(null, null, Integer.parseInt(appYear));
						searchDocument(name, date, appDate, 0, 12);
					} else if(appMonth.equals("-месяц-") && !day.equals("-день-")) {
						Date date = new Date(Integer.parseInt(day), month, Integer.parseInt(year));
						Date appDate = new Date(null, null, Integer.parseInt(appYear));
						searchDocument(name, date, appDate, 0, 12);
					} else if(day.equals("-день-") && appDay.equals("-день-")) {
						Date date = new Date(null, month, Integer.parseInt(year));
						Date appDate = new Date(null, appMonth, Integer.parseInt(appYear));
						searchDocument(name, date, appDate, 0, 12);
					} else if(day.equals("-день-") && !appDay.equals("-день-")) {
						Date date = new Date(null, month, Integer.parseInt(year));
						Date appDate = new Date(Integer.parseInt(appDay), appMonth, Integer.parseInt(appYear));
						searchDocument(name, date, appDate, 0, 12);
					} else if(!day.equals("-день-") && appDay.equals("-день-")) {
						Date date = new Date(Integer.parseInt(day), month, Integer.parseInt(year));
						Date appDate = new Date(null, appMonth, Integer.parseInt(appYear));
						searchDocument(name, date, appDate, 0, 12);
					} else {
						Date date = new Date(Integer.parseInt(day), month, Integer.parseInt(year));
						Date appDate = new Date(Integer.parseInt(appDay), appMonth, Integer.parseInt(appYear));
						searchDocument(name, date, appDate, 0, 12);
					}
				}
				//Поиск по названию, дате утверждения и преподавателю
				if(btnName.getSelection() && !btnDate.getSelection() && btnTeacher.getSelection() && btnAppDate.getSelection()) {
					if(name.equals("") || teach.equals("-преподаватель-") || appYear.equals("-год-")) {
						warningMessage("Заполните все поля!");
						openSearchWindow();
					} else if(month.equals("-месяц-")) {
						Date date = new Date(null, null, Integer.parseInt(appYear));
						int teachId = Teacher.getTeacherId(teach);
						searchDocument(name, null, date, teachId, 13);	
					} else if(day.equals("-день-")) {
						Date date = new Date(null, appMonth, Integer.parseInt(appYear));
						int teachId = Teacher.getTeacherId(teach);
						searchDocument(name, null, date, teachId, 13);
					} else {
						Date date = new Date(Integer.parseInt(appDay), appMonth, Integer.parseInt(appYear));
						int teachId = Teacher.getTeacherId(teach);
						searchDocument(name, null, date, teachId, 13);
					}
				}
				//Поиск по дате утверждения, дате и преподавателю
				if(!btnName.getSelection() && btnDate.getSelection() && btnTeacher.getSelection() && btnAppDate.getSelection()) {
					if(teach.equals("-преподаватель-") || year.equals("-год-") || appYear.equals("-год-")) {
						warningMessage("Заполните все поля!");
						openSearchWindow();
					} else if(month.equals("-месяц-") && appMonth.equals("-месяц-")) {
						int teachId = Teacher.getTeacherId(teach);
						Date date = new Date(null, null, Integer.parseInt(year));
						Date appDate = new Date(null, null, Integer.parseInt(appYear));
						searchDocument(null, date, appDate, teachId, 14);	
					} else if(month.equals("-месяц-") && appDay.equals("-день-")) {
						int teachId = Teacher.getTeacherId(teach);
						Date date = new Date(null, null, Integer.parseInt(year));
						Date appDate = new Date(null, appMonth, Integer.parseInt(appYear));
						searchDocument(null, date, appDate, teachId, 14);
					} else if(month.equals("-месяц-") && !appDay.equals("-день-")) {
						int teachId = Teacher.getTeacherId(teach);
						Date date = new Date(null, null, Integer.parseInt(year));
						Date appDate = new Date(Integer.parseInt(appDay), appMonth, Integer.parseInt(appYear));
						searchDocument(null, date, appDate, teachId, 14);
					} else if(appMonth.equals("-месяц-") && day.equals("-день-")) {
						int teachId = Teacher.getTeacherId(teach);
						Date date = new Date(null, month, Integer.parseInt(year));
						Date appDate = new Date(null, null, Integer.parseInt(appYear));
						searchDocument(null, date, appDate, teachId, 14);
					} else if(appMonth.equals("-месяц-") && !day.equals("-день-")) {
						int teachId = Teacher.getTeacherId(teach);
						Date date = new Date(Integer.parseInt(day), month, Integer.parseInt(year));
						Date appDate = new Date(null, null, Integer.parseInt(appYear));
						searchDocument(null, date, appDate, teachId, 14);
					} else if(day.equals("-день-") && appDay.equals("-день-")) {
						int teachId = Teacher.getTeacherId(teach);
						Date date = new Date(null, month, Integer.parseInt(year));
						Date appDate = new Date(null, appMonth, Integer.parseInt(appYear));
						searchDocument(null, date, appDate, teachId, 14);
					} else if(day.equals("-день-") && !appDay.equals("-день-")) {
						int teachId = Teacher.getTeacherId(teach);
						Date date = new Date(null, month, Integer.parseInt(year));
						Date appDate = new Date(Integer.parseInt(appDay), appMonth, Integer.parseInt(appYear));
						searchDocument(null, date, appDate, teachId, 14);
					} else if(!day.equals("-день-") && appDay.equals("-день-")) {
						int teachId = Teacher.getTeacherId(teach);
						Date date = new Date(Integer.parseInt(day), month, Integer.parseInt(year));
						Date appDate = new Date(null, appMonth, Integer.parseInt(appYear));
						searchDocument(null, date, appDate, teachId, 14);
					} else {
						int teachId = Teacher.getTeacherId(teach);
						Date date = new Date(Integer.parseInt(day), month, Integer.parseInt(year));
						Date appDate = new Date(Integer.parseInt(appDay), appMonth, Integer.parseInt(appYear));
						searchDocument(null, date, appDate, teachId, 14);
					}
				}
				//Поиск по названию, дате, преподавателю и дате утверждения
				if(btnName.getSelection() && btnDate.getSelection() && btnTeacher.getSelection() && btnAppDate.getSelection()) {
					if(name.equals("") || teach.equals("-преподаватель-") || year.equals("-год-") || appYear.equals("-год-")) {
						warningMessage("Заполните все поля!");
						openSearchWindow();
					} else if(month.equals("-месяц-") && appMonth.equals("-месяц-")) {
						int teachId = Teacher.getTeacherId(teach);
						Date date = new Date(null, null, Integer.parseInt(year));
						Date appDate = new Date(null, null, Integer.parseInt(appYear));
						searchDocument(name, date, appDate, teachId, 15);	
					} else if(month.equals("-месяц-") && appDay.equals("-день-")) {
						int teachId = Teacher.getTeacherId(teach);
						Date date = new Date(null, null, Integer.parseInt(year));
						Date appDate = new Date(null, appMonth, Integer.parseInt(appYear));
						searchDocument(name, date, appDate, teachId, 15);
					} else if(month.equals("-месяц-") && !appDay.equals("-день-")) {
						int teachId = Teacher.getTeacherId(teach);
						Date date = new Date(null, null, Integer.parseInt(year));
						Date appDate = new Date(Integer.parseInt(appDay), appMonth, Integer.parseInt(appYear));
						searchDocument(name, date, appDate, teachId, 15);
					} else if(appMonth.equals("-месяц-") && day.equals("-день-")) {
						int teachId = Teacher.getTeacherId(teach);
						Date date = new Date(null, month, Integer.parseInt(year));
						Date appDate = new Date(null, null, Integer.parseInt(appYear));
						searchDocument(name, date, appDate, teachId, 15);
					} else if(appMonth.equals("-месяц-") && !day.equals("-день-")) {
						int teachId = Teacher.getTeacherId(teach);
						Date date = new Date(Integer.parseInt(day), month, Integer.parseInt(year));
						Date appDate = new Date(null, null, Integer.parseInt(appYear));
						searchDocument(name, date, appDate, teachId, 15);
					} else if(day.equals("-день-") && appDay.equals("-день-")) {
						int teachId = Teacher.getTeacherId(teach);
						Date date = new Date(null, month, Integer.parseInt(year));
						Date appDate = new Date(null, appMonth, Integer.parseInt(appYear));
						searchDocument(name, date, appDate, teachId, 15);
					} else if(day.equals("-день-") && !appDay.equals("-день-")) {
						int teachId = Teacher.getTeacherId(teach);
						Date date = new Date(null, month, Integer.parseInt(year));
						Date appDate = new Date(Integer.parseInt(appDay), appMonth, Integer.parseInt(appYear));
						searchDocument(name, date, appDate, teachId, 15);
					} else if(!day.equals("-день-") && appDay.equals("-день-")) {
						int teachId = Teacher.getTeacherId(teach);
						Date date = new Date(Integer.parseInt(day), month, Integer.parseInt(year));
						Date appDate = new Date(null, appMonth, Integer.parseInt(appYear));
						searchDocument(name, date, appDate, teachId, 15);
					} else {
						int teachId = Teacher.getTeacherId(teach);
						Date date = new Date(Integer.parseInt(day), month, Integer.parseInt(year));
						Date appDate = new Date(Integer.parseInt(appDay), appMonth, Integer.parseInt(appYear));
						searchDocument(name, date, appDate, teachId, 15);
					}
				}
				dialog.close();
			}
		});		
		dialog.pack();
		dialog.open();
    }
    //Поиск
    private void searchDocument(String docName, Date docDate, Date appDate, int teach, int j) {
		table.removeAll();
		ArrayList<Document> docList = null;
		docList = Document.searchDocuments(docName, docDate, appDate, teach, j);	
		fillTable(docList);
    }
    //Журнал
    private void openLogWindow() {
    	final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    	Point pt = display.getCursorLocation();
    	dialog.setLocation(pt.x, pt.y);
    	dialog.setText("Журнал");
    	
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        gridLayout.marginHeight = gridLayout.marginWidth = 2;
        gridLayout.horizontalSpacing = gridLayout.verticalSpacing = 0;
        dialog.setLayout(gridLayout);
        
        final Table table = new Table(dialog, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
        table.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));

        String[] titles = new String[] {"Имя","Действие","Дата"};
    	int[] widths = new int[] { 150, 90, 90};
        for (int i = 0; i < titles.length; ++i) {
        	TableColumn column = new TableColumn(table, SWT.NONE);
        	column.setText(titles[i]);
        	column.setWidth(widths[i]);
        }
        table.setHeaderVisible(true);
        
        final Group gr = new Group(dialog, SWT.NONE);
		gr.setLayout(new RowLayout(SWT.HORIZONTAL));
		gr.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
        
        Button clear = new Button(gr, SWT.SINGLE | SWT.PUSH);
        clear.setText("Очистить");
        
        final Button del = new Button(gr, SWT.SINGLE | SWT.PUSH);
        del.setText("Удалить запись");
        del.setEnabled(false);
        
        Button close = new Button(gr, SWT.SINGLE | SWT.PUSH);
        close.setText("Закрыть");
        
        final Log log = new Log();
        
        clear.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Log.clearLog();
				getLog(table);
			}
        });
        del.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Log.deleteLog(log);
				getLog(table);
			}
        });
        close.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				dialog.close();
			}
		});
        table.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent event) {
				final TableItem[] items = table.getSelection();
        		if (items != null && items.length != 0) {
        			del.setEnabled(true);
        			TableItem item = items[0];
        			String doc = item.getText(0);
        			String action = item.getText(1);
        			String d = item.getText(2);
        			Date date = Date.convert(d);
        			log.setAction(action);
        			log.setDate(date);
        			log.setDocument(doc);
        		}					
			}
        });
        
        getLog(table);       
        dialog.pack();
        dialog.open();
    }
    //Журнал
    private void getLog(Table table) {
    	ArrayList<Log> logs = Log.getLog(); 	
    	table.removeAll();
    	
    	for(int i = 0;i < logs.size();i++) {
			Image image = null;
			String document = new String(logs.get(i).getDocument());
			String action = new String(logs.get(i).getAction());
			Date date = new Date(logs.get(i).getDate().getDay(), logs.get(i).getDate().getMonth(), logs.get(i).getDate().getYear());
			String type = logs.get(i).getDocType();
			
			if (type.equals("doc") || type.equals("docx")) {
				type = "Документ Microsoft Office Word";
				image = new Image(display, "microsoft-office-word.png");
			}	
			if (type.equals("pdf")) {
				type = "Документ Adobe Acrobat";
				image = new Image(display, "adobe-acrobat.png");
			}
			if (type.equals("djvu")) {
				type = "DjVu документ";
				image = new Image(display, "djvu.png");
			}
			
			final String[] strings = new String[] {document, action, date.toString()};
			TableItem tableItem = new TableItem(table, 0);
			tableItem.setText(strings);
			tableItem.setImage(image);
		}		
    }
    //Преподаватели
    private void teacherWindow() {
    	final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    	Point pt = display.getCursorLocation();
    	dialog.setLocation(pt.x, pt.y);
    	dialog.setText("Работа с информацией о преподавателях");
    	
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        gridLayout.marginHeight = gridLayout.marginWidth = 2;
        gridLayout.horizontalSpacing = gridLayout.verticalSpacing = 0;
        dialog.setLayout(gridLayout);
        
        final Table table = new Table(dialog, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
        table.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));

        TableColumn column = new TableColumn(table, SWT.NONE);
        column.setText("ФИО преподавателя");
        column.setWidth(200);
        
        table.setHeaderVisible(true);
        
        final Group gr = new Group(dialog, SWT.NONE);
		gr.setLayout(new RowLayout(SWT.HORIZONTAL));
		gr.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
        
        Button add = new Button(gr, SWT.SINGLE | SWT.PUSH);
        add.setText("Добавить");
        
        final Button del = new Button(gr, SWT.SINGLE | SWT.PUSH);
        del.setText("Удалить");
        del.setEnabled(false);
        
        Button close = new Button(gr, SWT.SINGLE | SWT.PUSH);
        close.setText("Закрыть");
        
        final Teacher t = new Teacher();
        
        add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addTeacher();
				getTeachers(table);
			}
        });
        del.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int i = Teacher.deleteTeacher(t);
				if(i == 1) {
					warningMessage("Нельзя удалить преподавателя, т.к. за ним есть закрепленные документы!");
				}
				getTeachers(table);
			}
        });
        close.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				dialog.close();
			}
		});
        table.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent event) {
				final TableItem[] items = table.getSelection();
        		if (items != null && items.length != 0) {
        			del.setEnabled(true);
        			TableItem item = items[0];
        			String fio = item.getText(0);
        			t.setName(fio);
        		}					
			}
        });
        
        getTeachers(table);
        
        dialog.pack();
        dialog.open();
    }
    //Изменить логин/пароль
    private void changeLoginPassword() {
    	final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    	Point pt = display.getCursorLocation();
    	dialog.setLocation(pt.x, pt.y);
    	dialog.setText("Изменить логин/пароль");
    	
    	GridLayout gridLayout = new GridLayout(2, false);
		dialog.setLayout(gridLayout);
		
		GridData data = new GridData();
		data.widthHint = 150;
		
		final Button btnLogin = new Button(dialog, SWT.CHECK);
		btnLogin.setText("Введите новый логин");
		
		final Text loginText = new Text(dialog, SWT.BORDER);
		loginText.setLayoutData(data);
		loginText.setVisible(false);
		
		final Button btnPass = new Button(dialog, SWT.CHECK);
		btnPass.setText("Введите новый пароль");
		
		final Text passText = new Text(dialog, SWT.BORDER | SWT.PASSWORD);
		passText.setLayoutData(data);
		passText.setVisible(false);
		
		final Button btnName = new Button(dialog, SWT.CHECK);
		btnName.setText("Введите имя");
		
		final Text nameText = new Text(dialog, SWT.BORDER);
		nameText.setLayoutData(data);
		nameText.setVisible(false);

		Button ok = new Button(dialog, SWT.PUSH);
		ok.setText("Готово");
		ok.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, true, true));
		
		Button cancel = new Button(dialog, SWT.PUSH);
		cancel.setText("Отмена");
		
		btnLogin.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(btnLogin.getSelection()) {
					loginText.setVisible(true);
				} else {
					loginText.setVisible(false);
				}
			}
		});
		btnPass.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(btnPass.getSelection()) {
					passText.setVisible(true);
				} else {
					passText.setVisible(false);
				}
			}
		});
		btnName.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(btnName.getSelection()) {
					nameText.setVisible(true);
				} else {
					nameText.setVisible(false);
				}
			}
		});

		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(btnLogin.getSelection() && !btnPass.getSelection() && !btnName.getSelection()) {
					String login = loginText.getText();
					if(login != ""){
						administrator = Admin.changeLogin(administrator, login);
						warningMessage("Логин изменен!");
					} else {
						warningMessage("Введите логин!");
						loginText.setText("");
					}
				}
				if(!btnLogin.getSelection() && btnPass.getSelection() && !btnName.getSelection()) {
					String pass = passText.getText();
					if(pass != ""){
						administrator = Admin.changePassword(administrator, pass);
						warningMessage("Пароль изменен!");
					} else {
						warningMessage("Введите пароль!");
						passText.setText("");
					}
				}
				if(!btnLogin.getSelection() && !btnPass.getSelection() && btnName.getSelection()) {
					String name = nameText.getText();
					if(name != ""){
						administrator = Admin.changeName(administrator, name);
						warningMessage("Имя изменено!");
					} else {
						warningMessage("Введите имя!");
						nameText.setText("");
					}
				}
				if(btnLogin.getSelection() && btnPass.getSelection() && !btnName.getSelection()) {
					String login = loginText.getText();
					String pass = passText.getText();
					if(login != "" && pass != ""){
						administrator = Admin.changeLogin(administrator, login);
						administrator = Admin.changePassword(administrator, pass);
						warningMessage("Информация изменена!");
					} else {
						warningMessage("Заполните поля!");
						loginText.setText("");
						passText.setText("");
					}
				}
				if(btnLogin.getSelection() && !btnPass.getSelection() && btnName.getSelection()) {
					String login = loginText.getText();
					String name = nameText.getText();
					if(login != "" && name != ""){
						administrator = Admin.changeLogin(administrator, login);
						administrator = Admin.changeName(administrator, name);
						warningMessage("Информация изменена!");
					} else {
						warningMessage("Заполните поля!");
						loginText.setText("");
						nameText.setText("");
					}
				}
				if(!btnLogin.getSelection() && btnPass.getSelection() && btnName.getSelection()) {
					String name = nameText.getText();
					String pass = passText.getText();
					if(name != "" && pass != ""){
						administrator = Admin.changeName(administrator, name);
						administrator = Admin.changePassword(administrator, pass);
						warningMessage("Информация изменена!");
					} else {
						warningMessage("Заполните поля!");
						nameText.setText("");
						passText.setText("");
					}
				}
				if(btnLogin.getSelection() && btnPass.getSelection() && btnName.getSelection()) {
					String login = loginText.getText();
					String pass = passText.getText();
					String name = nameText.getText();
					if(login != "" && pass != "" && name != ""){
						administrator = Admin.changeLogin(administrator, login);
						administrator = Admin.changePassword(administrator, pass);
						administrator = Admin.changeName(administrator, name);
						warningMessage("Информация изменена!");
					} else {
						warningMessage("Заполните поля!");
						loginText.setText("");
						passText.setText("");
						nameText.setText("");
					}
				}
			}
		});
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				dialog.close();
			}
		});
		dialog.pack();
		dialog.open();
    }
    //Вывести преподавателей
    private void getTeachers(Table table) {
    	ArrayList<Teacher> teachers = Teacher.getTeachers();
    	table.removeAll();
    	
    	for(int i = 0;i < teachers.size();i++) {
			String fio = new String(teachers.get(i).getName());
			TableItem tableItem = new TableItem(table, 0);
			tableItem.setText(fio);
		}		
    }
    //Добавить преподавателя
    private void addTeacher() {
    	final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    	Point pt = display.getCursorLocation();
    	dialog.setLocation(pt.x, pt.y);
    	dialog.setText("Добавить информацию о преподавателе");
    	
    	GridLayout gridLayout = new GridLayout(2, false);
		dialog.setLayout(gridLayout);
		
		GridData data = new GridData();
		data.widthHint = 150;
		
		Label label = new Label(dialog, SWT.NONE);
		label.setText("Введите ФИО");
		
		final Text text = new Text(dialog, SWT.BORDER);
		text.setLayoutData(data);

		Button ok = new Button(dialog, SWT.PUSH);
		ok.setText("Готово");

		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if(!text.getText().equals("")) {
					Teacher.addTeacher(text.getText());
				} else {
					warningMessage("Введите ФИО преподавателя!");
					addTeacher();
				}
				dialog.close();
			}
		});
		dialog.pack();
		dialog.open();
    }
       
    //Создаем дерево
	private void createTreeView(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        gridLayout.marginHeight = gridLayout.marginWidth = 2;
        gridLayout.horizontalSpacing = gridLayout.verticalSpacing = 0;
        composite.setLayout(gridLayout);
        
        treeScopeLabel = new Label(composite, SWT.BORDER);
        treeScopeLabel.setText("Все каталоги");
        treeScopeLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));

        tree = new Tree(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE);
        tree.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		ArrayList<Catalog> cat = Catalog.getCatalogsByParent(0);
		CatalogTree root = new CatalogTree(cat.get(0));
		int level = Catalog.getMaxLevel();
    	addChildren(root, level);
        setTreeContents(root);
        tree.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent event) {
        		final TreeItem[] selection = tree.getSelection();
        		if (selection != null && selection.length != 0) {
        			TreeItem item = selection[0];
					getDocuments(item.getText());
        		}
        	}
        });
    }
	//Заполняем дерево каталогов
	private void setTreeContents(CatalogTree root) {
		tree.removeAll();
	    TreeItem ti = new TreeItem(tree, SWT.NONE);
	    setTreeItemContents(ti, root);
	}
	//Заполняем дерево каталогов
	private void setTreeItemContents(TreeItem ti, CatalogTree root) {
		Image image = new Image(display, "images/folder-open.png");
	    ti.setText(root.getCatalog().getName());
	    ti.setImage(image);
	    java.util.List<CatalogTree> children = root.getChildren();
	    if (children != null && children.size() > 0) {
	        for (Iterator<CatalogTree> i = children.iterator(); i.hasNext();) {
	            CatalogTree ct = i.next();
	            TreeItem tix = new TreeItem(ti, SWT.NONE);
	            setTreeItemContents(tix, ct);
	        }
	    }
	}
	//Заполняем дерево каталогов
	private void addChildren(CatalogTree ct, int level){
    	ArrayList<Catalog> cat = Catalog.getCatalogsByParent(ct.getCatalog().getIdCatalog());
        for (int i = 0; i < cat.size(); i++) {
    		Catalog catalog = cat.get(i);
    		CatalogTree child = new CatalogTree(catalog);
    		ct.addChild(child);
    		addChildren(child, level - 1);
		}
	}
	//Создаем таблицу
    private void createTableView(Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        gridLayout.marginHeight = gridLayout.marginWidth = 2;
        gridLayout.horizontalSpacing = gridLayout.verticalSpacing = 0;
        composite.setLayout(gridLayout);
        tableContentsOfLabel = new Label(composite, SWT.BORDER);
        tableContentsOfLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));

        table = new Table(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
        table.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));

        for (int i = 0; i < tableTitles.length; ++i) {
        	TableColumn column = new TableColumn(table, SWT.NONE);
        	column.setText(tableTitles[i]);
        	column.setWidth(tableWidths[i]);
        }
        table.setHeaderVisible(true);
        table.addMouseListener(new MouseAdapter() {
			public void mouseDoubleClick(org.eclipse.swt.events.MouseEvent event) {
				final TableItem[] items = table.getSelection();
	    		if (items != null && items.length != 0) {
	    			TableItem item = items[0];
	    			String text = item.getText();
	    			if (!Document.isDocument(text)) {
	    				getDocuments(text);
	        		} else {
	    				try {
							openDocument(text);
						} catch (IOException e) {
							e.printStackTrace();
						}
	    			}
	    		}
			}
			public void mouseDown(MouseEvent event) {
				if(event.button == 3) {
					final TableItem[] items = table.getSelection();
	        		if (items != null && items.length != 0) {
	        			TableItem item = items[0];
	        			String name = item.getText();
	        			String date = item.getText(1);
	        			if(administrator.getLogin() != null) {
	        				createPopupMenuView(shell, name, date);
	        			}
	        		}
				}
			}
        });
    }
    //Открыть документ
    private void openDocument(String name) throws IOException {
    	String address = path + File.separator + Document.getDocumentsAddress(name);
    	Desktop desktop = null;
    	if (Desktop.isDesktopSupported()) {
    		desktop = Desktop.getDesktop();
    		if (desktop.isSupported(Desktop.Action.OPEN))
        		desktop.open(new File(address));
    	}    	
    }
    //Вывести документы
    private void getDocuments(String catalog){
    	if(catalog == null)
    		return;
    	currentCatalog = catalog;
    	table.removeAll();    	
    	int id_catalog = Catalog.getCatalogIdByName(catalog);
    	ArrayList<Catalog> catList = Catalog.getCatalogsByParent(id_catalog);
    	ArrayList<Document> docList = Document.getDocumentsByCatalog(id_catalog);
    	
		for(int i = 0;i < catList.size();i++) {
			Image image = new Image(display, "images/folder-open.png");
			String name = new String(catList.get(i).getName());
			final String[] strings = new String[] {name, "", "", "", "Папка с файлами", ""};
			TableItem tableItem = new TableItem(table, 0);
			tableItem.setText(strings);
			tableItem.setImage(image);
		}
		fillTable(docList);	
    }
    //Заполнить таблицу
    private void fillTable(ArrayList<Document> docList) {
    	for(int i = 0;i < docList.size();i++) {
			Image image = null;
			String name = new String(docList.get(i).getName());
			Date date = new Date(docList.get(i).getDate().getDay(), docList.get(i).getDate().getMonth(), docList.get(i).getDate().getYear());
			Date approvalDate = new Date(docList.get(i).getApprovalDate().getDay(), docList.get(i).getApprovalDate().getMonth(), docList.get(i).getApprovalDate().getYear());
			Double version = docList.get(i).getVersion();
			String type = new String(docList.get(i).getType());
			String author = new String(Teacher.getTeacherFIO(docList.get(i).getIdTeacher()));
			if (type.equals("doc") || type.equals("docx")) {
				type = "Документ Microsoft Office Word";
				image = new Image(display, "images/microsoft-office-word.png");
			}	
			if (type.equals("pdf")) {
				type = "Документ Adobe Acrobat";
				image = new Image(display, "images/adobe-acrobat.png");
			}
			if (type.equals("djvu")) {
				type = "DjVu документ";
				image = new Image(display, "images/djvu.png");
			}
			final String[] strings = new String[] {name, date.toString(), approvalDate.toString(), version.toString(), type, author};
			TableItem tableItem = new TableItem(table, 0);
			tableItem.setText(strings);
			tableItem.setImage(image);
		}		
    }
    
    private void createPopupMenuView(final Shell shell, final String name, final String date) {
    	Menu menu = new Menu(shell, SWT.POP_UP);
		table.setMenu(menu);
		MenuItem mi = new MenuItem(menu, SWT.PUSH);
		mi.setText("Удалить");
		mi.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				MessageBox mb = new MessageBox(shell, SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL);
				mb.setText("Подтверждение удаления");
				mb.setMessage("Вы уверены, что хотите удалить " + name + "?");
				int answ = mb.open();
				if(answ == SWT.OK) {
					if(Document.isDocument(name)) {
						Document doc = Document.getDocumentByName(name);
						File file = new File(path + File.separator + Document.getDocumentsAddress(name));
						Date date = Date.getCurrentDate();
						Log log = new Log();
						log.setDocument(name);
						log.setDocType(doc.getType());
						log.setAction("Удален");
						log.setDate(date);
						Log.addLog(log);
						Document.deleteDocument(name);
						file.delete();
					}
					else {
						int i = Catalog.deleteCatalog(name);
						if(i == 0) {
							File directory = new File(path + File.separator + Catalog.getPath(currentCatalog) + name);
							directory.delete();
						} else {
							warningMessage("Нельзя удалить каталог!");
						}
					}
					refresh();
				}
			}
		});
		MenuItem mi1 = new MenuItem(menu, SWT.PUSH);
		mi1.setText("Добавить папку");
		mi1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				addFolder();
			}
		});
		if(!date.equals("")) {
			MenuItem mi2 = new MenuItem(menu, SWT.PUSH);
			mi2.setText("Добавить дату утверждения");
			mi2.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					addApprovalDate(name);
				}
			});
		}
    }
    
    private void addApprovalDate(final String doc) {
    	final Shell dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    	Point pt = display.getCursorLocation();
    	dialog.setLocation(pt.x, pt.y);
    	dialog.setText("Поиск");
    	Integer[] days = new Integer [31];
    	String[] monthes = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
    	Integer[] years = new Integer [20];
    	
    	for(Integer i = 0; i < days.length; i++)
    		days[i] = new Integer (i+1);
    	for(Integer i = 0; i < years.length; i++) {
    		Calendar cal = Calendar.getInstance();
    		years[i] = new Integer (cal.get(Calendar.YEAR) - i);
    	}	
    	
    	GridLayout gridLayout = new GridLayout(2, false);
		dialog.setLayout(gridLayout); 
		
		final Label labelDate = new Label(dialog, SWT.LEFT);
		labelDate.setText("Введите дату утверждения");
		
		final Group gr = new Group(dialog, SWT.NONE);
		gr.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		final Combo comboDay = new Combo(gr, SWT.DROP_DOWN);
		comboDay.setText("-день-");
		for(int i = 0; i < days.length; i++) 
			comboDay.add(days[i].toString());
		
		final Combo comboMonth = new Combo(gr, SWT.DROP_DOWN);
		comboMonth.setText("-месяц-");
		for(int i = 0; i < monthes.length; i++) 
			comboMonth.add(monthes[i]);
		
		final Combo comboYear = new Combo(gr, SWT.DROP_DOWN);
		comboYear.setText("-год-");
		for(int i = 0; i < years.length; i++) 
			comboYear.add(years[i].toString());

		Button ok = new Button(dialog, SWT.SINGLE | SWT.PUSH);
		ok.setText("Готово");
		ok.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, true, true));
		
		Button cancel = new Button(dialog, SWT.SINGLE | SWT.PUSH);
		cancel.setText("Отмена");
		
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String day = comboDay.getText();
				String month = comboMonth.getText();
				String year = comboYear.getText();
				if(day.equals("-день-") || month.equals("-месяц-") || year.equals("-год-")) {
					warningMessage("Введите дату полностью!");
				} else {
					Date date = new Date(Integer.parseInt(day), month, Integer.parseInt(year));
					Document.setApprovalDate(doc, date);
					refresh();
					dialog.close();
				}
			}
		});
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				dialog.close();
			}
		});
		
		dialog.pack();
		dialog.open();
    }
    
    private void refresh() {
    	ArrayList<Catalog> cat = Catalog.getCatalogsByParent(0);
		CatalogTree root = new CatalogTree(cat.get(0));
		int level = Catalog.getMaxLevel();
    	addChildren(root, level);
        setTreeContents(root);
		getDocuments(currentCatalog);
    }
    
    private void getPath() {
    	path = null;
    	String progPath = System.getProperty("user.dir");
    	try (BufferedReader br = new BufferedReader(new FileReader(progPath + File.separator + "path.txt"))) {
    		path = br.readLine();
    	} catch (IOException e) {
    		createPath();
    	} 
    }
    
    private void createPath() {
    	path = null;
    	String progPath = System.getProperty("user.dir");
    	try {
	    	File file = new File(progPath + File.separator + "path.txt");
	    	if(file.createNewFile()) {
	    		System.out.println("File is created");
	    		DirectoryDialog dialog = new DirectoryDialog(shell);
	        	String platform = SWT.getPlatform();
	        	dialog.setFilterPath(platform.equals("win32") || platform.equals("wpf") ? "c:\\" : "/");
	        	dialog.setText("Выберите папку для хранения документации");
	        	path = dialog.open();
	    	}
	    	FileWriter fw = new FileWriter(file.getAbsoluteFile());
	    	BufferedWriter bw = new BufferedWriter(fw);
	    	bw.write(path);
	    	bw.close();
    	} catch (IOException e) {
    		e.printStackTrace();  		
    	}
    }
    
    private void warningMessage(String message) {
    	MessageBox mb = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
		mb.setText("Предупреждение");
		mb.setMessage(message);
		mb.open();
    }
    
    class ProgressDialog {
        public final static int COPY = 0;
        public final static int DELETE = 1;
        public final static int MOVE = 2;
        Shell shell;
        Label messageLabel, detailLabel;
        ProgressBar progressBar;
        Button cancelButton;
        boolean isCancelled = false;
        final String operationKeyName[] = { "Copy", "Delete", "Move" };

        public ProgressDialog(Shell parent, int style) {
        	shell = new Shell(parent, SWT.BORDER | SWT.TITLE | SWT.APPLICATION_MODAL);
        	GridLayout gridLayout = new GridLayout();
        	shell.setLayout(gridLayout);
        	shell.setText("progressDialog."  + operationKeyName[style] + ".title");
        	shell.addShellListener(new ShellAdapter() {
        		public void shellClosed(ShellEvent e) {
        			isCancelled = true;
        		}
        	});

        	messageLabel = new Label(shell, SWT.HORIZONTAL);
        	messageLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));
        	messageLabel.setText("progressDialog." + operationKeyName[style] + ".description");

        	progressBar = new ProgressBar(shell, SWT.HORIZONTAL | SWT.WRAP);
        	progressBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));
        	progressBar.setMinimum(0);
        	progressBar.setMaximum(0);

        	detailLabel = new Label(shell, SWT.HORIZONTAL);
        	GridData gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
        	gridData.widthHint = 400;
        	detailLabel.setLayoutData(gridData);

        	cancelButton = new Button(shell, SWT.PUSH);
        	cancelButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_FILL));
        	cancelButton.setText("progressDialog.cancelButton.text");
        	cancelButton.addSelectionListener(new SelectionAdapter() {
        		public void widgetSelected(SelectionEvent e) {
        			isCancelled = true;
        			cancelButton.setEnabled(false);
        		}
        	});
        }

        public boolean isCancelled() {
        	return isCancelled;
        }

        public void setTotalWorkUnits(int work) {
        	progressBar.setMaximum(work);
        }

        public void addWorkUnits(int work) {
        	setTotalWorkUnits(progressBar.getMaximum() + work);
        }

        public void setProgress(int work) {
        	progressBar.setSelection(work);
        	while (display.readAndDispatch()) {
        	} // enable event processing
        }

        public void addProgress(int work) {
        	setProgress(progressBar.getSelection() + work);
        }

        public void open() {
        	shell.pack();
        	final Shell parentShell = (Shell) shell.getParent();
        	Rectangle rect = parentShell.getBounds();
        	Rectangle bounds = shell.getBounds();
        	bounds.x = rect.x + (rect.width - bounds.width) / 2;
        	bounds.y = rect.y + (rect.height - bounds.height) / 2;
        	shell.setBounds(bounds);
        	shell.open();
        }

        public void close() {
        	shell.close();
        	shell.dispose();
        	shell = null;
        	messageLabel = null;
        	detailLabel = null;
        	progressBar = null;
        	cancelButton = null;
        }
     }
}