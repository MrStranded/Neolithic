package main.data.infrastructure;

import main.data.ID;

import java.util.Iterator;
import java.util.LinkedList;
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
public class BinaryTree<T extends ID> implements Iterable {

	// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

	/**
	 * Internal class to manage the node data.
	 */
	private class TreeNode<T extends ID> {
		private int id;
		private T value;
		private TreeNode left = null, right = null;

		public TreeNode(T value) {
			setValue(value);
		}

		/**
		 * Assign new value to this node.
		 *
		 * @param value to be assigned
		 */
		public void setValue(T value) {
			this.value = value;
			id = value.getId();
		}

		public T getValue() {
			return value;
		}
	}
	// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

	private TreeNode root = null;

	public BinaryTree() {
	}

	/**
	 * Inserts the given value in the correct place in the binary tree.
	 *
	 * @param value to insert
	 */
	public void add(T value) {
		if (root == null) {
			root = new TreeNode(value);
		} else {
			put(root, value);
		}
	}

	/**
	 * Either inserts value at correct position of current Node
	 * or recursively searches the tree for the correct place.<br>
	 * ATTENTION: Replaces values with same id. (should it maybe add them?)
	 *
	 * @param node      that is currently checked for insertion possibilities
	 * @param value     to insert
	 */
	private void put(TreeNode node, T value) {
		// Left
		if (value.getId() < node.id) {
			if (node.left == null) {
				node.left = new TreeNode(value);
			} else {
				put(node.left, value);
			}
		}

		// Right
		if (value.getId() > node.id) {
			if (node.right == null) {
				node.right = new TreeNode(value);
			} else {
				put(node.right, value);
			}
		}

		// Same id - add or replace? (replace)
		node.setValue(value);
	}

	/**
	 * Initializes the search for the requested value and returns it.
	 *
	 * @param id requested id
	 * @return value if found, null otherwise
	 */
	public T get(int id) {
		return searchId(root, id);
	}

	/**
	 * Recursively searches the binary tree for the value with the requested id.
	 *
	 * @param node current Node to search
	 * @param id   requested id
	 * @return value if found, null otherwise
	 */
	private T searchId(TreeNode node, int id) {
		if (node == null) {
			return null;
		}

		if (id < node.id) {
			return searchId(node.left, id);
		}

		if (id > node.id) {
			return searchId(node.right, id);
		}

		return (T) node.getValue();
	}

	// ###################################################################################
	// ################################ Iterable methods #################################
	// ###################################################################################

	// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

	private class BinaryTreeIterator implements Iterator<T> {

		LinkedList<T> attributes;
		Iterator<T> iterator;

		public BinaryTreeIterator(BinaryTree tree) {
			attributes = new LinkedList<>();

			addElement(root);

			iterator = attributes.listIterator(0);
		}

		private void addElement(TreeNode currentNode) {
			if (currentNode != null) {
				attributes.add((T) currentNode.getValue());

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
		public T next() {
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
		public void forEachRemaining(Consumer<? super T> action) {
			// nono
		}
	}
	// &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

	@Override
	public Iterator<T> iterator() {
		return new BinaryTreeIterator(this);
	}

}
