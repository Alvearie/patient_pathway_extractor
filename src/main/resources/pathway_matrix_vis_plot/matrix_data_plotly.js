var trace1 = {
	name : 'conditions',
	x : [ '0', '1', '2' ],
	y : [ '0', '2', '2' ],
	z : [ 'con', 'con', 'con' ],
	text : [ 'T1A', 'T1B', 'T1C' ],
	mode : 'markers',
	marker : {
		color : 'rgb(153,216,201)',
		size : 14,
		line : {
			color : 'rgb(153,216,201)',
			width : 0.5
		},
		opacity : 0.8
	},
	type : 'scatter3d'
};
var trace2 = {
	name : 'medications',
	x : [ 1, 2 ],
	y : [ 2, 1 ],
	z : [ 'med', 'med' ],
	text : [ 'T2A', 'T2B' ],
	mode : 'markers',
	marker : {
		color : 'rgb(44,162,95)',
		size : 14,
	// line : {
	// color : 'rgb(44,162,95)',
	// width : 0.5
	// },
	// opacity : 0.8
	},
	type : 'scatter3d'
};
var data = [ trace1, trace2 ];