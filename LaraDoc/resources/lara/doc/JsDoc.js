function update_doc(doc_link) {
	//document.getElementById("doc_frame").src=doc_link; 
	//document.getElementById("doc_content").innerHTML='<object type="text/html" data="' + doc_link +  '" ></object>';
	$.ajax({
            url : doc_link,
            dataType: "text",
            success : function (data) {
                $("#doc_content").html(data);
            }
        });
}