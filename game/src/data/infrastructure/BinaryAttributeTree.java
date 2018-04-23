package data.infrastructure;

import data.personal.Attribute;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * A kinda lightweight binary search tree for Attributes.<br>
 * OPEN QUESTIONS:<br>
 * - prevent degeneration?<br>
 * - generalize binary tree?<br>
 * - replace already existing Attributes in tree or add them (currently add)?<br>
 * <p>
 * Created by michael1337 on 20/04/18.
 */
public class BinaryAttributeTree implements Iterable<Attribute> {

	// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

	/**
	 * Internal class to manage the node data.
	 */
	private class AttributeNode {
		private int id;
		private Attribute attribute;
		private AttributeNode left = null, right = null;

		public AttributeNode(Attribute attribute) {
			setAttribute(attribute);
		}

		/**
		 * Assign new Attribute to this node.
		 *
		 * @param attribute to be assigned
		 */
		public void setAttribute(Attribute attribute) {
			this.attribute = attribute;
			id = attribute.getId();
		}
	}
	// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

	private AttributeNode root = null;

	public BinaryAttributeTree() {
	}

	/**
	 * Inserts the given Attribute in the correct place in the binary tree.
	 *
	 * @param attribute to insert
	 */
	public void add(Attribute attribute) {
		if (root == null) {
			root = new AttributeNode(attribute);
		} else {
			put(root, attribute);
		}
	}

	/**
	 * Either inserts attribute at correct position of current Node
	 * or recursively searches the tree for the correct place.<br>
	 * ATTENTION: Adds Attributes with same id. (should it maybe replace them?)
	 *
	 * @param node      that is currently checked for insertion possibilities
	 * @param attribute to insert
	 */
	private void put(AttributeNode node, Attribute attribute) {
		// Left
		if (attribute.getId() < node.id) {
			if (node.left == null) {
				node.left = new AttributeNode(attribute);
			} else {
				put(node.left, attribute);
			}
		}

		// Right
		if (attribute.getId() > node.id) {
			if (node.right == null) {
				node.right = new AttributeNode(attribute);
			} else {
				put(node.right, attribute);
			}
		}

		// Same id - add or replace? (add)
		node.attribute.add(attribute);
	}

	/**
	 * Initializes the search for the requested Attribute and returns it.
	 *
	 * @param id requested id
	 * @return Attribute if found, null otherwise
	 */
	public Attribute get(int id) {
		return searchId(root, id);
	}

	/**
	 * Recursively searches the binary tree for the Attribute with the requested id.
	 *
	 * @param node current Node to search
	 * @param id   requested id
	 * @return Attribute if found, null otherwise
	 */
	private Attribute searchId(AttributeNode node, int id) {
		if (node == null) {
			return null;
		}

		if (id < node.id) {
			return searchId(node.left, id);
		}

		if (id > node.id) {
			return searchId(node.right, id);
		}

		return node.attribute;
	}

	// ###################################################################################
	// ################################ Iterable methods #################################
	// ###################################################################################

	// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

	private class BinaryAttributeTreeIterator implements Iterator<Attribute> {

		LinkedList<Attribute> attributes;
		Iterator<Attribute> iterator;

		public BinaryAttributeTreeIterator(BinaryAttributeTree tree) {
			attributes = new LinkedList<>();

			addElement(root);

			iterator = attributes.listIterator(0);
		}

		private void addElement(AttributeNode currentNode) {
			if (currentNode != null) {
				attributes.add(currentNode.attribute);

				if (currentNode.left != null) {
					addElement(currentNode.left);
				}
				if (currentNode.right != null) {
					addElement(currentNode.right);
				}
			}
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public Attribute next() {
			return iterator.next();
		}

		/**
		 * ATTENTION:
		 * The remove method is not functional!!!
		 * It does not do a thing.
		 */
		@Override
		public void remove() {
			// nono
		}

		/**
		 * ATTENTION:
		 * The forEachRemaining method is not functional!!!
		 * It does not do a thing.
		 */
		@Override
		public void forEachRemaining(Consumer<? super Attribute> action) {
			// nono
		}
	}
	// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

	@Override
	public Iterator<Attribute> iterator() {
		return new BinaryAttributeTreeIterator(this);
	}

	/**
	 * NOT IMPLEMENTED!!!
	 * @param action
	 */
	@Override
	public void forEach(Consumer<? super Attribute> action) {

	}

	/**
	 * NOT IMPLEMENTED!!!
	 * @return
	 */
	@Override
	public Spliterator<Attribute> spliterator() {
		return null;
	}
}
