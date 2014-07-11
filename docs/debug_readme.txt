NODE EXPLORER

belief 		- Display the agent's current belief state.  This requires a StateParser and if one hasn't been made, this will cause a NullPointer
children	- Display the children (actions or observations) of the current node.
0, 1, 2...	- Move to the child node indexed at the number entered
stop		- Leave the node explorer and allow the agent to continue

USERENVIRONMENT

You will be prompted with the current real state and asked to enter an observation.  This must be the string name of an observation associated
with the domain, and must have a nonzero probability of being observed from the current state, or else the agent will hang.  

Enter an empty string (just click ENTER) to allow the domain to generate an observation instead of entering one.
