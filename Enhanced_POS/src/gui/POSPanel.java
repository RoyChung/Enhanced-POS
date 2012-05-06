package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import core.Controller;
import core.entities.Item;
import core.entities.ItemList;

public class POSPanel extends JPanel {

	private JTextField idInput;
	private JTextField amountIDInput;
	private JTextField amountClickInput;
	private JButton addButton;
	private JButton toRightButton;
	private JButton toLeftButton;
	private JButton clearButton;

	private JList<String> productList;
	private JList<String> shoppingCartList;
	private DefaultListModel<String> shoppingCartListModel;
	
	public POSPanel() {
		idInput = new JTextField(20);
		amountIDInput = new JTextField(20);
		amountClickInput = new JTextField(20);
		addButton = new JButton("Add");
		toRightButton = new JButton("-->");
		toLeftButton = new JButton("<--");
		clearButton = new JButton("Clear");

		JLabel idLabel = new JLabel("ID:");
		JLabel amountLabel = new JLabel("Amount:");
		JPanel idInputPanel = new JPanel();
		idInputPanel.add(idLabel);
		idInputPanel.add(idInput);
		idInputPanel.add(amountLabel);
		idInputPanel.add(amountIDInput);
		idInputPanel.add(addButton);
		
		DefaultListModel<String> productListModel = new DefaultListModel<String>();
		HashMap<String, Item> products = ItemList.getInstance().getItems();
		Iterator<Entry<String, Item>> it = products.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Item> pairs = (Map.Entry<String, Item>) it.next();
			productListModel.addElement(pairs.getValue().getItemName());
		}
		productList = new JList<String>(productListModel);
		productList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		productList.setVisibleRowCount(20);
		JScrollPane selectPane = new JScrollPane(productList);
		
		JPanel selectButtonsPanel = new JPanel();
		selectButtonsPanel.setLayout(new GridLayout(5, 1, 5, 5));
		selectButtonsPanel.add(amountLabel);
		selectButtonsPanel.add(amountClickInput);
		selectButtonsPanel.add(toRightButton);
		selectButtonsPanel.add(toLeftButton);
		selectButtonsPanel.add(clearButton);
		
		shoppingCartListModel = new DefaultListModel<String>();
		shoppingCartList = new JList<String>(shoppingCartListModel);
		JScrollPane shoppingCartPane = new JScrollPane(shoppingCartList);
		
		
		toRightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String selectedItemName = productList.getSelectedValue();
				Item selectedItem = ItemList.getInstance().getItemByName(selectedItemName);
				addToCart(selectedItem, Integer.valueOf(amountClickInput.getText()));
			}
		});
		
		toLeftButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				removeFromCart();
			}
		});
		
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Item item = ItemList.getInstance().getItemById(idInput.getText());
				addToCart(item, Integer.valueOf(amountIDInput.getText()));
			}
		});

		
		setLayout(new BorderLayout());
		add(idInputPanel, BorderLayout.NORTH);
		add(selectPane, BorderLayout.WEST);
		add(selectButtonsPanel, BorderLayout.CENTER);
		add(shoppingCartPane, BorderLayout.EAST);

	}
	
	private void addToCart(Item item, int amount) {
		Controller.getInstance().addToCart(item, amount);
		updateShoppingCartList();
	}
	
	private void removeFromCart() {
		String selectedItemName = shoppingCartList.getSelectedValue().split(" ")[0];
		Item selectedItem = ItemList.getInstance().getItemByName(selectedItemName);
		Controller.getInstance().removeFromCart(selectedItem);
		updateShoppingCartList();
	}
	
	private void updateShoppingCartList() {
		shoppingCartListModel.clear();
		DecimalFormat df = Controller.getInstance().getNumberFormat();
		Iterator<Entry<String, Integer>> it = Controller.getInstance().getOrderList().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Integer> pairs = it.next();
			Item i = ItemList.getInstance().getItemById(pairs.getKey());
			float subTotal = i.getPrice() * pairs.getValue();
			String cartDisplay = i.getItemName() + " "  + pairs.getValue() + " = " + df.format(subTotal); 
			shoppingCartListModel.addElement(cartDisplay);
		}
	}
}