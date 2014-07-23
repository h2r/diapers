package edu.brown.h2r.diapers.solver.datastructures;

import java.util.Map;
import java.util.HashMap;

public class SearchTreeNode {
	protected Map<SearchTreeEdge, SearchTreeNode> children;

	{
		children = new HashMap<SearchTreeEdge, SearchTreeNode>();
	}

	public SearchTreeNode advance(SearchTreeEdge ste) {
		return children.get(ste);
	}

	public void addChild(SearchTreeEdge ste) {
		children.put(ste, new SearchTreeNode());
	}
	
	public boolean isLeaf() {
		return children.isEmpty();
	}

	public Map<SearchTreeEdge, SearchTreeNode> asMap() {
		return children;
	}
}
