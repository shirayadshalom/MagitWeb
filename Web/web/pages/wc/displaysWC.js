var FIlE_FOLDERS_LIST_URL=buildUrlWithContextPath("commit/files");
var BLOB_LIST_URL=buildUrlWithContextPath("blob/content");
var repo_name;
var beforeModifiedFileContent
var sha1_;
var selectedPath;


//["c:\\magit-ex3\\itamar\\rep 2,folder","c:\\magit-ex3\\itamar\\rep 2\\a.txt,blob","c:\\magit-ex3\\itamar\\rep 2\\fol1,folder","c:\\magit-ex3\\itamar\\rep 2\\fol1\\foo.java,blob","c:\\magit-ex3\\itamar\\rep 2\\fol2,folder","c:\\magit-ex3\\itamar\\rep 2\\fol2\\b.txt,blob","c:\\magit-ex3\\itamar\\rep 2\\fol2\\fol3,folder","c:\\magit-ex3\\itamar\\rep 2\\\\fol2\\fol3\\c.txt,blob"]
function refreshWC(list) {
    $.each(list || [], function(index,data) {
        console.log("Adding user #" + index + ": " +  data);
        var data_array = data.split(',');
        var path=data_array[0];
        var type=data_array[1];
        var tr="<tr onclick=myFunction($(this).find(\"td[id='path']\").text(),$(this).find(\"td[id='type']\").text());>";
        var td1="<td id=\"path\">"+path+"</td>";
        var td2="<td id=\"type\">"+type+"</td> </tr>";
        $("#WCTable").find('tbody').append(tr+td1+td2);

        console.log(this);
    });

}

function myFunction(path,type) {
    console.log("path: " + path);
    console.log("type: " + type);
    selectedPath=path;
    if (type == "blob") {
        ajaxBlob();
        document.getElementById("myForm1").style.display = "block";
    }

}


function ajaxBlob(){
    $.ajax({
        url: BLOB_LIST_URL,
        data: {path: selectedPath},
        success: function(data) {
            beforeModifiedFileContent=data;
            refreshBlob(beforeModifiedFileContent);
        },
        error: function (thrownError) {
            alert(thrownError);
        }
    });
}

function refreshBlob(beforeModifiedFileContent) {
    console.log(beforeModifiedFileContent);
    //document.getElementById("blob").innerHTML = beforeModifiedFileContent;
    $("#blob").val(beforeModifiedFileContent);

}

function closeForm1() {
    document.getElementById("myForm1").style.display = "none";
}

function ajaxWC(){
$.ajax({
    url: FIlE_FOLDERS_LIST_URL,
    data: {repo: repo_name,
        sha1: sha1_},
    dataType: "json",
    success: function(data) {
        //["c:\\magit-ex3\\itamar\\rep 2,folder","c:\\magit-ex3\\itamar\\rep 2\\a.txt,blob","c:\\magit-ex3\\itamar\\rep 2\\fol1,folder","c:\\magit-ex3\\itamar\\rep 2\\fol1\\foo.java,blob","c:\\magit-ex3\\itamar\\rep 2\\fol2,folder","c:\\magit-ex3\\itamar\\rep 2\\fol2\\b.txt,blob","c:\\magit-ex3\\itamar\\rep 2\\fol2\\fol3,folder","c:\\magit-ex3\\itamar\\rep 2\\\\fol2\\fol3\\c.txt,blob"]
        refreshWC(data);
    }
});
}


$(function() {
    var parts = location.search.substring(1).split('&');
    var arr= parts[0].split('=');
    repo_name =decodeURI(arr[1]);
    sha1_=decodeURI(location.search.split('sha1=')[1]);
    //repo_name=decodeURI(location.search.split("&")[1]);
    //sha1_=decodeURI(location.search.split('sha1=')[1]);
    console.log(repo_name);
    console.log(sha1_);
    ajaxWC();
});


