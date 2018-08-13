package engine.data.structures;

import engine.data.IDInterface;
import engine.data.attributes.Attribute;

/**
 * My own implementation of an AVL tree.
 */
public class BinaryTree<T extends IDInterface> {

	private BinaryTreeNode root;

	private static final int LEFT = 0;
	private static final int RIGHT = 1;

	public BinaryTree() {
	}

	// ###################################################################################
	// ################################ Insertion ########################################
	// ###################################################################################

	public void insert(T value) {
		if (value == null) {
			return; // we have to go sure that nodes ALWAYS hold a value != null
		}

		if (root == null) {
			root = new BinaryTreeNode(value);
		} else {
			insert(value, root);
			sanitize();
		}
	}

	private void insert(T value, BinaryTreeNode node) {
		if (value.getId() < node.getValue().getId()) {
			if (node.getLeft() == null) {
				node.setLeft(new BinaryTreeNode(value));
			} else {
				insert(value, node.getLeft());
			}
		} else if (value.getId() > node.getValue().getId()) {
			if (node.getRight() == null) {
				node.setRight(new BinaryTreeNode(value));
			} else {
				insert(value, node.getRight());
			}
		} else { // ids are equal -> merge
			node.setValue(node.getValue().merge(value));
		}
	}

	// ###################################################################################
	// ################################ Balancing ########################################
	// ###################################################################################

	public void sanitize() {
		root = checkNode(root);
	}

	private BinaryTreeNode checkNode(BinaryTreeNode node) {
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

	private BinaryTreeNode rotateNode(BinaryTreeNode node, int direction) {
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

	private int getDepth(BinaryTreeNode node) {
		if (node == null) {
			return 0;
		}
		return Math.max(getDepth(node.getLeft()), getDepth(node.getRight())) + 1;
	}

	public int getDepth() {
		return getDepth(root);
	}

	// ###################################################################################
	// ################################ Deletion #########################################
	// ###################################################################################

	// ###################################################################################
	// ################################ Retrieval ########################################
	// ###################################################################################

	public T get(int id) {
		BinaryTreeNode node = root;

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

	public int size() {
		return size(root);
	}

	private int size(BinaryTreeNode node) {
		if (node == null) {
			return 0;
		}
		return size(node.getLeft()) + size(node.getRight()) + 1;
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
				return node.getValue().getId() + "<" + getDepth(node) + ">: (" + subString(node.getLeft()) + " , " + subString(node.getRight()) + ")";
			} else {
				return String.valueOf(node.getValue().getId());
			}
		}
	}
}
