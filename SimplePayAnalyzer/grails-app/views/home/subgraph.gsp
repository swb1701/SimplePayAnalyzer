<%@page defaultCodec="none"%>
<html>
<head>
<meta name="layout" content="main" />
<title>Active Subscriptions</title>
<script src="${resource(dir: 'js', file: 'd3.min.js')}"></script>
<style>
.chart rect {
	fill: steelblue;
}

.chart text {
	fill: white;
	font: 10px sans-serif;
	text-anchor: end;
}

.chart .label {
	fill: black;
	font: 10px sans-serif;
	text-anchor: start;
}

.chart .date {
	fill: black;
	font: 10px sans-serif;
	text-anchor: start;
}

</style>
</head>
<body>
	<div class="body" style="margin-top:10px;margin-left:10px;">
		<svg class="chart"></svg>
		<script>
$(function() {
  var data=${data as grails.converters.JSON};
  data=data.sort(function(a,b) {if (b.amount==a.amount) {return a.fromDate.localeCompare(b.fromDate); } else {return b.amount-a.amount;}});
  var pwidth=$('.body').width();
  var width=0.7*pwidth;
  var barHeight=12;
  var x=d3.scale.linear().range([0,width]);
  var chart = d3.select(".chart").attr("width",pwidth);
  var max=d3.max(data,function(d) { return d.amount; });
  x.domain([0,d3.max(data,function(d) { return d.amount; })]);
  chart.attr("height",barHeight*data.length);
  var offset=80;
  var bar = chart.selectAll("g")
    .data(data)
	.enter().append("g")
  	.attr("transform", function(d, i) { return "translate(0," + i * barHeight + ")"; });

  bar.append("rect")
    .attr("x",offset)
 	.attr("width", function(d) { return x(d.amount); })
	.attr("height", barHeight - 1);

  bar.append("text")
	.attr("x", function(d) { return offset+x(d.amount) - 3; })
	.attr("y", barHeight / 2)
	.attr("dy", ".35em")
	.text(function(d) { return '$'+d.amount; });
	
  bar.append("text")
    .attr("class","label")
	.attr("x", function(d) { return offset+x(d.amount) +3; })
	.attr("y", barHeight / 2)
	.attr("dy", ".35em")
	.text(function(d) { return d.name; });

  bar.append("text")
    .attr("class","date")
	.attr("x", function(d) { return 3; })
	.attr("y", barHeight / 2)
	.attr("dy", ".35em")
	.text(function(d,i) { return d.fromDate+" #"+(i+1); });
	
});
  </script>
	</div>
</body>
</html>