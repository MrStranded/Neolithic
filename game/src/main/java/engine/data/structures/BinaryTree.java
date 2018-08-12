package engine.data.structures;

import engine.data.IDInterface;
import engine.data.attributes.Attribute;

/**
 * My own implementation of an AVL tree.
 */
public class BinaryTree<T extends IDInterface> {

	private BinaryTreeNode root;

	private static final int LEFT = 0;
	private static final int RIGHT = 0;

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
			root = checkNode(root);
		}
	}

	private void insert(T value, BinaryTreeNode node) {
		if (value.getId() < node.getValue().getId()) {
			if (node.getLeft() == null) {
				node.setLeft(new BinaryTreeNode(value));
				recalculateDepth(root);
			} else {
				insert(value, node.getLeft());
				node.setLeft(checkNode(node.getLeft()));
			}
		} else if (value.getId() > node.getValue().getId()) {
			if (node.getRight() == null) {
				node.setRight(new BinaryTreeNode(value));
				recalculateDepth(root);
			} else {
				insert(value, node.getRight());
				node.setRight(checkNode(node.getRight()));
			}
		} else { // ids are equal -> merge
			node.setValue(node.getValue().merge(value));
		}
	}

	// ###################################################################################
	// ################################ Balancing ########################################
	// ###################################################################################

	private BinaryTreeNode checkNode(BinaryTreeNode node) {
		int difference = getDepth(node.getRight()) - getDepth(node.getLeft());
		if (difference > 1) { // right arm is too long
			System.out.println("right inba "+node.getValue().getId());

			if (node.getLeft() == null) {
				return rotateNode(node, LEFT);
			} else {
				int subDifference = getDepth(node.getLeft().getRight()) - getDepth(node.getLeft().getLeft());
				if (subDifference < 0) {
					node.setLeft(rotateNode(node.getLeft(), RIGHT));
				}
				return rotateNode(node, LEFT);
			}
		} else if (difference < -1) { // left arm is too long
			System.out.println("left inba"+node.getValue().getId());

			if (node.getRight() == null) {
				return rotateNode(node, RIGHT);
			} else {
				int subDifference = getDepth(node.getLeft().getRight()) - getDepth(node.getLeft().getLeft());
				if (subDifference > 0) {
					node.setRight(rotateNode(node.getRight(), LEFT));
				}
				return rotateNode(node, RIGHT);
			}
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
		return node.getDepth();
	}

	private int recalculateDepth(BinaryTreeNode node) {
		if (node == null) {
			return 0;
		}
		int depth = Math.max(recalculateDepth(node.getLeft()), recalculateDepth(node.getRight())) + 1;
		node.setDepth(depth);
		return depth;
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
