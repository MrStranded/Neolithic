package engine.data.structures;

import engine.data.IDInterface;
import engine.data.attributes.Attribute;

public class BinaryTreeNode<T extends IDInterface> {

	private BinaryTreeNode left, right;
	private T value;
	private int depth = 0;

	public BinaryTreeNode(T value) {
		this.value = value;
	}

	// ###################################################################################
	// ################################ Getters and Setters ##############################
	// ###################################################################################

	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}

	public BinaryTreeNode getLeft() {
		return left;
	}
	public void setLeft(BinaryTreeNode left) {
		this.left = left;
	}

	public BinaryTreeNode getRight() {
		return right;
	}
	public void setRight(BinaryTreeNode right) {
		this.right = right;
	}

	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
}
