var FIlE_FOLDERS_LIST_URL=buildUrlWithContextPath("commit/files");
var BLOB_LIST_URL=buildUrlWithContextPath("blob/content");
var DELETE_URL=buildUrlWithContextPath("blob/delete");
var SAVE_NEW_FILE_URL=buildUrlWithContextPath("blob/create");
var repo_name;
var selectedPath;
var modifiedFileContent;
var newFileContents;
var newFilePath;
var beforeModifiedFileContent





//["c:\\magit-ex3\\itamar\\rep 2,folder","c:\\magit-ex3\\itamar\\rep 2\\a.txt,blob","c:\\magit-ex3\\itamar\\rep 2\\fol1,folder","c:\\magit-ex3\\itamar\\rep 2\\fol1\\foo.java,blob","c:\\magit-ex3\\itamar\\rep 2\\fol2,folder","c:\\magit-ex3\\itamar\\rep 2\\fol2\\b.txt,blob","c:\\magit-ex3\\itamar\\rep 2\\fol2\\fol3,folder","c:\\magit-ex3\\itamar\\rep 2\\\\fol2\\fol3\\c.txt,blob"]
function refreshWC(list) {
    $("#WCTable").find('tbody').empty();
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


function CreatenewFile(){
    document.getElementById("myForm").style.display = "block";
}



function onDelete() {
    console.log("path: " + selectedPath);
    if(selectedPath== undefined){
        alert("nothing selected!");
    }
    else {
    ajaxDelete(selectedPath);}
}

function ajaxDelete(path_){
    $.ajax({
        url: DELETE_URL,
        data: {path: path_},
        success: function(data) {
            ajaxWC()
        },


    });
}


function refreshBlob(beforeModifiedFileContent) {
        console.log(beforeModifiedFileContent);
        //document.getElementById("blob").innerHTML = beforeModifiedFileContent;
        $("#blob").val(beforeModifiedFileContent);

}

function ajaxModifiedfile(){
    $.ajax({
        url: SAVE_NEW_FILE_URL,
        data: {path: selectedPath,
            content: modifiedFileContent},
        success: function() {
            //["c:\\magit-ex3\\itamar\\rep 2,folder","c:\\magit-ex3\\itamar\\rep 2\\a.txt,blob","c:\\magit-ex3\\itamar\\rep 2\\fol1,folder","c:\\magit-ex3\\itamar\\rep 2\\fol1\\foo.java,blob","c:\\magit-ex3\\itamar\\rep 2\\fol2,folder","c:\\magit-ex3\\itamar\\rep 2\\fol2\\b.txt,blob","c:\\magit-ex3\\itamar\\rep 2\\fol2\\fol3,folder","c:\\magit-ex3\\itamar\\rep 2\\\\fol2\\fol3\\c.txt,blob"]
            ajaxWC();
            closeForm1();
        }
    });
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

function ajaxsaveNewFile(){
    $.ajax({
        url: SAVE_NEW_FILE_URL,
        data: {path: newFilePath,
            content: newFileContents},
        success: function() {
            //["c:\\magit-ex3\\itamar\\rep 2,folder","c:\\magit-ex3\\itamar\\rep 2\\a.txt,blob","c:\\magit-ex3\\itamar\\rep 2\\fol1,folder","c:\\magit-ex3\\itamar\\rep 2\\fol1\\foo.java,blob","c:\\magit-ex3\\itamar\\rep 2\\fol2,folder","c:\\magit-ex3\\itamar\\rep 2\\fol2\\b.txt,blob","c:\\magit-ex3\\itamar\\rep 2\\fol2\\fol3,folder","c:\\magit-ex3\\itamar\\rep 2\\\\fol2\\fol3\\c.txt,blob"]
            ajaxWC();
            closeForm();
        }
    });
}


function ajaxWC(){
    $.ajax({
        url: FIlE_FOLDERS_LIST_URL,
        data: {repo: repo_name,
            sha1: "wc"},
        dataType: "json",
        success: function(data) {
            //["c:\\magit-ex3\\itamar\\rep 2,folder","c:\\magit-ex3\\itamar\\rep 2\\a.txt,blob","c:\\magit-ex3\\itamar\\rep 2\\fol1,folder","c:\\magit-ex3\\itamar\\rep 2\\fol1\\foo.java,blob","c:\\magit-ex3\\itamar\\rep 2\\fol2,folder","c:\\magit-ex3\\itamar\\rep 2\\fol2\\b.txt,blob","c:\\magit-ex3\\itamar\\rep 2\\fol2\\fol3,folder","c:\\magit-ex3\\itamar\\rep 2\\\\fol2\\fol3\\c.txt,blob"]
            refreshWC(data);
        }
    });
}


$(function() {
    repo_name=decodeURI(location.search.split('repo=')[1]);
    console.log(repo_name);
    ajaxWC();
});

function closeForm() {
    document.getElementById("myForm").style.display = "none";
}

function closeForm1() {
    document.getElementById("myForm1").style.display = "none";
}

function saveNewFile() {
    newFileContents=document.getElementById("textbox").value;
    console.log(newFileContents);
    newFilePath=document.getElementById("newPath").value;
    console.log(newFilePath);
    ajaxsaveNewFile();
}

function saveFile() {
    modifiedFileContent=document.getElementById("blob").value;
    console.log(modifiedFileContent);
    ajaxModifiedfile();
}

function goBack() {
    window.open("../repository/repository.html?data="+repo_name, "_self","toolbar=yes,scrollbars=yes,resizable=yes,top=500,left=500,width=600,height=600");
}



