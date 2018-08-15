package engine.data.structures;

import engine.data.IDInterface;
import engine.data.attributes.Attribute;

/**
 * My own implementation of an AVL tree.
 */
public class BinaryTree<T extends IDInterface> {

	private BinaryTreeNode<T> root;

	private static final int LEFT = 0;
	private static final int RIGHT = 1;

	public BinaryTree() {
	}

	// ###################################################################################
	// ################################ Insertion ########################################
	// ###################################################################################

	/**
	 * Inserts a value into the tree and balances the tree.
	 * If a value with the same id is already present, the two values get merged (IDInterface.merge()).
	 * @param value to insert
	 */
	public void insert(T value) {
		if (value == null) {
			return; // we have to go sure that nodes ALWAYS hold a value != null
		}

		if (root == null) {
			root = new BinaryTreeNode<T>(value);
		} else {
			insert(value, root);
			sanitize();
		}
	}

	/**
	 * Inserts multiple values into the tree and balances the tree.
	 * If a value with the same id is already present, the two values get merged (IDInterface.merge()).
	 * @param values to insert
	 */
	public void insert(T[] values) {
		if (values == null) {
			return; // no values to add
		}

		for (T value : values) {
			if (value != null) { // go sure value is not null! very important for the tree to work
				if (root == null) {
					root = new BinaryTreeNode<T>(value);
				} else {
					insert(value, root);
				}
			}
		}
		sanitize();
	}

	/**
	 * Inserts the contents of another Binary Tree into this tree.
	 * Afterwards the tree is being balanced.
	 * @param tree whose values to insert
	 */
	public void insert(BinaryTree<T> tree) {
		if (tree != null) {
			insertNode(tree.root);
			sanitize();
		}
	}

	private void insertNode(BinaryTreeNode<T> node) {
		if (node == null) {
			return;
		}

		insert(node.getValue());
		insertNode(node.getRight());
		insertNode(node.getLeft());
	}

	private void insert(T value, BinaryTreeNode<T> node) {
		if (value.getId() < node.getValue().getId()) {
			if (node.getLeft() == null) {
				node.setLeft(new BinaryTreeNode<T>(value));
			} else {
				insert(value, node.getLeft());
			}
		} else if (value.getId() > node.getValue().getId()) {
			if (node.getRight() == null) {
				node.setRight(new BinaryTreeNode<T>(value));
			} else {
				insert(value, node.getRight());
			}
		} else { // ids are equal -> merge
			node.setValue((T) node.getValue().merge(value));
		}
	}

	// ###################################################################################
	// ################################ Balancing ########################################
	// ###################################################################################

	/**
	 * This method balances the binary tree.
	 */
	private void sanitize() {
		root = checkNode(root);
	}

	private BinaryTreeNode<T> checkNode(BinaryTreeNode<T> node) {
		if (node == null) { // nothing to check
			return null;
		}

		if (node.getRight() != null) { // make a sub balancing act
			node.setRight(checkNode(node.getRight()));
		}
		if (node.getLeft() != null) { // make a sub balancing act
			node.setLeft(node.getLeft());
		}

		int difference = getDepth(node.getRight()) - getDepth(node.getLeft());
		if (difference > 1) { // right arm is too long
			// maybe we first have to rotate sub tree
			int subDifference = getDepth(node.getRight().getRight()) - getDepth(node.getRight().getLeft());
			if (subDifference < 0) {
				node.setRight(rotateNode(node.getRight(), RIGHT));
			}

			BinaryTreeNode result = rotateNode(node, LEFT);
			result.setLeft(checkNode(result.getLeft())); // errors might have entered the left sub tree
			return result;
		} else if (difference < -1) { // left arm is too long
			// maybe we first have to rotate sub tree
			int subDifference = getDepth(node.getLeft().getRight()) - getDepth(node.getLeft().getLeft());
			if (subDifference > 0) {
				node.setLeft(rotateNode(node.getLeft(), LEFT));
			}

			BinaryTreeNode result = rotateNode(node, RIGHT);
			result.setRight(checkNode(result.getRight())); // errors might have entered the right sub tree
			return result;
		}

		return node;
	}

	private BinaryTreeNode<T> rotateNode(BinaryTreeNode<T> node, int direction) {
		if (direction == LEFT) {
			BinaryTreeNode right = node.getRight();

			node.setRight(right.getLeft());
			right.setLeft(node);
			return right;
		} else {
			BinaryTreeNode left = node.getLeft();

			node.setLeft(left.getRight());
			left.setRight(node);
			return left;
		}
	}

	private int getDepth(BinaryTreeNode<T> node) {
		if (node == null) {
			return 0;
		}
		return Math.max(getDepth(node.getLeft()), getDepth(node.getRight())) + 1;
	}

	/**
	 * Returns the depth of the whole tree. An empty tree has depth 0.
	 * Depth of the tree is defined as the depth of the deepest node in the tree.
	 * @return depth
	 */
	public int getDepth() {
		return getDepth(root);
	}

	// ###################################################################################
	// ################################ Deletion #########################################
	// ###################################################################################

	/**
	 * Removes the value with the given id from the tree if present and balances the tree.
	 * @param id of value to remove
	 */
	public void remove(int id) {
		remove(id, root, null);
		sanitize();
	}

	/**
	 * This method is responsible for finding the node to remove and to remember the node that came previously.
	 * @param id
	 * @param node
	 * @param previous
	 */
	private void remove(int id, BinaryTreeNode<T> node, BinaryTreeNode<T> previous) {
		if (node == null) { // nothing to remove
			return;
		}

		if (id > node.getValue().getId()) {
			remove(id, node.getRight(), node);
		} else if (id < node.getValue().getId()) {
			remove(id, node.getLeft(), node);
		} else { // we found the node to remove!
			suckUp(id, node, previous);
		}
	}

	/**
	 * This method actually does the removing.
	 * Either it sucks up a value from a lower node and replaces the value to remove with the sucked up one,
	 * whilst also updating the references of other nodes to the one that has been sucked up.
	 * Or it sees that there are no values to suck up, in which case it just deletes the node.
	 * @param id
	 * @param node
	 * @param previous
	 */
	private void suckUp(int id, BinaryTreeNode<T> node, BinaryTreeNode<T> previous) {
		if (node == null) { // nothing to suck up
			return;
		}

		int depthRight = getDepth(node.getRight());
		int depthLeft = getDepth(node.getLeft());

		if (depthRight >= depthLeft) {
			if (depthRight == 0) { // no children to pick from -> remove node
				if (previous == null) { // we're still operating at the root
					root = null;
				} else { // remove node from previous' memory
					if (previous.getRight() == node) {
						previous.setRight(null);
					} else {
						previous.setLeft(null);
					}
				}

			} else { // we take a value from the right
				node.setValue(splitClosestValue(id, node.getRight(), node));
			}

		} else { // we take a value from the left
			node.setValue(splitClosestValue(id, node.getLeft(), node));
		}
	}

	/**
	 * Starting from node, it searches the node with the closest value to a given id.
	 * When found, it updates nodes that point to the closest value node, effectively cutting out the node.
	 * Finally it returns the closest value.
	 * @param id
	 * @param node may not be null!
	 * @param previous may not be null!
	 * @return value with closest id in subtree
	 */
	private T splitClosestValue(int id, BinaryTreeNode<T> node, BinaryTreeNode<T> previous) {
		BinaryTreeNode<T> closerChild;

		while ((closerChild = getCloserChild(id, node)) != null) {
			previous = node;
			node = closerChild;
		}

		if (previous.getRight() == node) { // split right child
			if (node.getValue().getId() < id) { // we need to stitch node.left to previous
				previous.setRight(node.getLeft());
			} else { // we need to stitch node.right to previous
				previous.setRight(node.getRight());
			}
		} else {
			if (node.getValue().getId() < id) { // we need to stitch node.left to previous
				previous.setLeft(node.getLeft());
			} else { // we need to stitch node.right to previous
				previous.setLeft(node.getRight());
			}
		}

		return (T) node.getValue();
	}

	/**
	 * Checks wheter there is a child with a closer value to id than the parent.
	 * If present, returns the child.
	 * @param id
	 * @param node may not be null!
	 * @return closest child, if present
	 */
	private BinaryTreeNode<T> getCloserChild(int id, BinaryTreeNode<T> node) {
		int distance = Math.abs(id - node.getValue().getId());

		if (node.getRight() != null) {
			if (Math.abs(node.getRight().getValue().getId() - id) < distance) { // right child is closer
				return node.getRight();
			}
		}
		if (node.getLeft() != null) {
			if (Math.abs(node.getLeft().getValue().getId() - id) < distance) { // left child is closer
				return node.getLeft();
			}
		}
		return null; // no closer children
	}

	// ###################################################################################
	// ################################ Retrieval ########################################
	// ###################################################################################

	/**
	 * Searches the tree for a value with the given id and returns it.
	 * Returns null if there is no value with requested id.
	 * @param id
	 * @return value with id, if present
	 */
	public T get(int id) {
		BinaryTreeNode<T> node = root;

		while (node != null && node.getValue().getId() != id) {
			if (node.getValue().getId() < id) {
				node = node.getRight();
			} else {
				node = node.getLeft();
			}
		}

		if (node != null) {
			return (T) node.getValue();
		} else {
			return null;
		}
	}

	/**
	 * Calculates the size of the tree, say the number of values in it.
	 * @return size of tree
	 */
	public int size() {
		return size(root);
	}

	private int size(BinaryTreeNode<T> node) {
		if (node == null) {
			return 0;
		}
		return size(node.getLeft()) + size(node.getRight()) + 1;
	}

	// ###################################################################################
	// ################################ Conversion #######################################
	// ###################################################################################

	/**
	 * Creates an array of length size() and fills it with the values in the tree.
	 * Note: the array is sorted. Values with lower IDs appear first, such with higher IDs later.
	 * @return
	 */
	public IDInterface[] toArray() {
		IDInterface[] array = new IDInterface[size()];
		if (root != null) {
			addToArray(root, array, 0);
		}
		return array;
	}

	private int addToArray(BinaryTreeNode<T> node, IDInterface[] array, int index) {
		if (node.getLeft() != null) {
			index = addToArray(node.getLeft(), array, index);
		}
		array[index++] = node.getValue();
		if (node.getRight() != null) {
			index = addToArray(node.getRight(), array, index);
		}
		return index;
	}

	// ###################################################################################
	// ################################ String Output ####################################
	// ###################################################################################

	public String toString() {
		return subString(root);
	}

	private String subString(BinaryTreeNode node) {
		if (node == null) {
			return ".";
		} else {
			if (node.getLeft() != null || node.getRight() != null) {
				return node.getValue().getId() + ": (" + subString(node.getLeft()) + " , " + subString(node.getRight()) + ")";
			} else {
				return String.valueOf(node.getValue().getId());
			}
		}
	}
}
