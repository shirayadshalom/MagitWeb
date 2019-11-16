var refreshRate = 2000;
var CHAT_LIST_URL = buildUrlWithContextPath("/chat");
var chatVersion = 0;
var BRANCH_LIST_URL = buildUrlWithContextPath("/branch/brancheslist");
var HEADBRANCH_URL=buildUrlWithContextPath("branch/headbranch");
var COM_TABLE_URL=buildUrlWithContextPath("commit/comlist");
var NEW_BRANCH_URL=buildUrlWithContextPath("branch/newbranch");
var CHECKOUT_URL=buildUrlWithContextPath("branch/checkout");
var CHANGED_URL=buildUrlWithContextPath("commit/changes");
var COM__URL=buildUrlWithContextPath("/commit");
var USERNAME_URL=buildUrlWithContextPath("/userName");
var ISRTB__URL=buildUrlWithContextPath("/isRTB");
var ISHEADPUSHED__URL=buildUrlWithContextPath("/isHeadPushed");
var PULL__URL=buildUrlWithContextPath("remote/pull");
var PUSH_URL=buildUrlWithContextPath("remote/push");
var IS_RR_URL=buildUrlWithContextPath("repo/isRR");
var PR__URL=buildUrlWithContextPath("/remote/PR");
var PR_LIST_URL=buildUrlWithContextPath("/remote/PRlist");
var NOTIFICATION_URL=buildUrlWithContextPath("/notifications");
var wereChanges;
var isRTB;
var isHeadPushed;
var headBranchName ;
var commitMessage;
var remoteRepo_name;
var repo_name;
var user;
var remoteRepo_username;
var branch_target;
var branch_base;






// ["master","new","deep"...]
function refreshBranchesList(branches) {
    $("#brancheslist").empty();
    $.each(branches || [], function(index,branchname) {
        console.log("Adding user #" + index + ": " +  branchname);
        if (headBranchName===branchname){
            $('<li><a href="#" >' + branchname +" "+"-Head"+ '</a></li>').appendTo($("#brancheslist"));
        }
        else{
            $('<li><a href="#" >' + branchname + '</a></li>').appendTo($("#brancheslist"));
        }
        console.log(this);
    });

}

function refreshComTable(commits) {
    /* [ { "SHA-1":"35333ERd33", "Message":"bbb","Commit date": "11/2/29", "Creator":"Or", "List of branches":["master","new","deep"...]},... ] */
   // $("tbody").children().remove();
    $("#ComTable").find('tbody').children().remove();
    $.each(commits || [], function(index, data) {
        console.log(index + ": " + data);
        var tr="<tr onclick=onClickCom($(this).find(\"td[id='SHA-1']\").text());>";
        var td1="<td id=\"SHA-1\">"+data["SHA-1"]+"</td>";
        var td2="<td>"+data["Message"]+"</td>";
        var td3="<td>"+data["Commit date"]+"</td>";
        var td4="<td>"+data["Creator"]+"</td>";
        var branches= data["List of branches"]
        var td5="<td>"+branches+"</td></tr>";
        $("#ComTable").find('tbody').append(tr+td1+td2+td3+td4+td5);
    });
    ajaxChangesFiles();
}

function refreshChangesFiles(files) {
    /* [ { "name: new1,type:blob","name: new2,type:folder"... ] */
    /*
    [[1:  {"Type (Folder or Blob): Blob","Name: c.txt","Who made the last change: itamar","When was the last update: 13.10.2019-10:18:33:035"}
    [2: {"Type (Folder or Blob): Blob","Name: c.txt","Who made the last change: itamar","When was the last update: 13.10.2019-10:18:33:035"}*/
    $.each(files || [], function(key,value ) {
        $("#updated").empty();
        $("#deleted").empty();
        $("#created").empty();
        var created="Created files:" +'<br>'
        var updated="Updated files:" +'<br>'
        var deleted="Deleted files:" +'<br>'
        console.log( key + ": " + value );
        console.log(files[1]);
        console.log(files[2]);
        console.log(files[3]);
        created=created.concat(files[1]);
        $("#created").append(created);
        updated=updated.concat(files[2]);
        $("#updated").append(updated);
        deleted=deleted.concat(files[3]);
        $("#deleted").append(deleted);
    });
    /*
   $.each(files || [], function(index, data) {
       $("#updated").empty();
       $("#deleted").empty();
       $("#created").empty();
       var created="Created files:" +'<br>'
       var updated="Updated files:" +'<br>'
       var deleted="Deleted files:" +'<br>'


       created=created.concat(lst1.valueOf());
       $("#created").append(created);
       var lst2 = data[1];
       updated=updated.concat(lst2);
       $("#updated").append(updated);
       var lst3 = data[2];
       deleted=deleted.concat(lst3);
       $("#deleted").append(deleted);
   });
   */
}

function onClickCom(sha1_) {
    window.open("../wc/displaysWC.html?repo="+repo_name+"&sha1="+sha1_,"toolbar=yes,scrollbars=yes,resizable=yes,top=500,left=500,width=600,height=600");}

function refreshHeadBranch(name) {
    headBranchName=name.trim();
    console.log(headBranchName);
}

function ajaxBranchesList(){
    $.ajax({
        url:
        BRANCH_LIST_URL,
        data: {repo: repo_name,
                username:user},
        dataType: "json",
        success: function(data) {
            // ["master","new","deep"...]
            refreshBranchesList(data);
        }
    });
    setTimeout("ajaxBranchesList()",5000);
}

function ajaxCheckOut(branch_name){
    $.ajax({
        url: CHECKOUT_URL,
        type: "get",
        data: {repo:repo_name,
            branchname:branch_name },
        success: function(data) {
            console.log(data.trim());
            if (data.trim().toString() === "success") {
            refreshHeadBranch(branch_name);
            ajaxBranchesList();
            }
            else
                alert("this branch doesnt exists!")
        }
    });

}


function ajaxNewBranch(branch_name){
    $.ajax({
        url: NEW_BRANCH_URL,
        type: "get",
        data: {repo:repo_name,
            branchname:branch_name },
        success: function(data) {
            console.log(data);
            if (data.trim().toString() === "success")
                ajaxBranchesList();
            else
                alert("this branch already exists!")
        }
    });

}

function ajaxChangesFiles(){
    $.ajax({
        url: CHANGED_URL,
        type: "get",
        data: {repo:repo_name,},
        dataType: "json",
        success: function(data) {
            /* [ { "name: new1,type:blob","name: new2,type:folder"... ] */
            refreshChangesFiles(data);
        }
    });

}

function ajaxComTable(){
    $.ajax({
        url: COM_TABLE_URL,
        type: "get",
        data: {repo:repo_name,
            branch:headBranchName },
        dataType: "json",
        success: function(data) {
            /* [ { "SHA-1":"35333ERd33", "Message":"bbb","Commit date": "11/2/29", "Creator":"Or", "List of branches":["master","new","deep"...]},... ] */
            refreshComTable(data);
        }
    });

}

function ajaxisRTB(){
    $.ajax({
        url: ISRTB__URL,
        type: "get",
        data: {repo:repo_name,},
        success: function(data) {
            console.log(data);
            if (data.trim().toString() === "success"){
                isRTB=true;
            }
            else
                isRTB=false;
        }
    });

}

function ajaxisHeadPushed(){
    $.ajax({
        url: ISHEADPUSHED__URL,
        type: "get",
        data: {repo:repo_name,
            RRusername: remoteRepo_username},
        success: function(data) {
            console.log(data);
            if (data.trim().toString() === "success"){
                isHeadPushed=true;
            }
            else
                isHeadPushed=false;
        }
    });
}

function ajaxCheckChanges(){
    $.ajax({
        url: CHANGED_URL,
        type: "get",
        data: {repo:repo_name,},
        success: function(data) {
            console.log(data);
           // {1: Array(0), 2: Array(0), 3: Array(0)}
            console.log(data[1]);
            console.log( data[1].length);
            console.log(data[2]);
            console.log( data[2].length);
            console.log(data[3]);
            console.log( data[3].length);
            if(data[1].length==0&&data[2].length==0&&data[3].length==0){
              wereChanges=false;
            }
            else{
                wereChanges=true;}
        }
    });
}

function ajaxPR(message_){
    $.ajax({
        url: PR__URL,
        type: "get",
        data: {repo:repo_name,
            RRusername:remoteRepo_username,
            RRname:remoteRepo_name,
            branchTarget :branch_target,
            branchBase:branch_base,
            message:message_},
        success: function(data) {
                alert("The PR sent")
        }
    });
}


function ajaxPull(){
    $.ajax({
        url: PULL__URL,
        type: "get",
        data: {repo:repo_name},
        success: function(data) {
            ajaxComTable();
        }
    });
}

function appendToChatArea(entries) {

    $.each(entries || [], appendChatEntry);
    var scroller = $("#chatarea");
    var height = scroller[0].scrollHeight - $(scroller).height();
    $(scroller).stop().animate({ scrollTop: height }, "slow");
}

function appendChatEntry(index, entry){
    var entryElement = createChatEntry(entry);
    $("#chatarea").append(entryElement).append("<br>");
}

function createChatEntry (entry){
    entry.chatString = entry.chatString.replace (":)", "<img class='smiley-image' src='../../common/images/smiley.png'/>");
    return $("<span class=\"success\">").append(entry.username + "> " + entry.chatString);
}

function ajaxCommit(){
    $.ajax({
        url: COM__URL,
        type: "get",
        data: {repo:repo_name,
            message:commitMessage},
        success: function(data) {
            console.log(data);
            if (data.trim().toString() === "success"){
                alert("success!")
                ajaxComTable();
            }
            else
                alert("No change from the last commit! Not created new Commit!")
        }
    });

}

function ajaxRRBranches(){
    $.ajax({
        url: BRANCH_LIST_URL,
        data: {repo: remoteRepo_name,
                username:remoteRepo_username },
        dataType: "json",
        success: function(data) {
            // ["master","new","deep"...]
            $("#userNameRR").text("Branches of "+remoteRepo_username+" -RR");
            refreshRRBranches(data);
            alert("Please select the base branch!")
        }
    });
}

function ajaxNotifications() {
    $.ajax({
        url: NOTIFICATION_URL,
        success: function(lst) {
            /*
            {"forks":["rep 1 was forked by itamar2",...]
            "prs":[prdata,prdata...]}*/
            refreshNotifications(lst);
        }
    });
}

function refreshNotifications(lst) {
    /*
      {"forks":["rep 1 was forked by itamar2",...]
      "prs":[prdata,prdata...]}
      */
    $("#notice_fork").empty();
    $("#notice_PR").empty();
    $.each(lst || [], function(index, data) {
        var notice_fork = "This is a fork notice:" + '<br>';
        var notice_pr = "This is a PR notice:" + '<br>';
        if (index=== "forks"){
            console.log(data);
            notice_fork=notice_fork.concat(data);
            $("#notice_fork").append(notice_fork);
        }
        if (index=== "PRs"){
            console.log(data);
            notice_pr=notice_pr.concat(data);
            $("#notice_PR").append(notice_pr);
        }
    });
}

function ajaxLRBranches(){
    $.ajax({
        url: BRANCH_LIST_URL,
        data: {repo:repo_name,
            username:user },
        dataType: "json",
        success: function(data) {
            // ["master","new","deep"...]
            $("#userNameLR").text("Branches of "+user+" -LR");
            refreshLRBranches(data);
            alert("Please select the target branch!")
        }
    });
}

function refreshPRList(lst){
    //   "ID: " + id +"," + "LR: " + lr.getName() + "," + "RR: " + rr.getName() + "," + "message : " + message + "," + "base : " + base.getName() + "," + "target : " + target.getName() + "," + "date : " + date + "," + "status : " + status;
    // [{"username: shir","branchPurpose: koko","branchBase: new", "PRDate: 18:50:00 10.10.19","Status:open"},{....}]
    $("#PRlist").find('tbody').children().remove();
    $.each(lst || [], function(index,data) {
        console.log(index + ": " + data);
        var parts = data.split(',');
        var ID=parts[0].split(':');
        var username=parts[1].split(':');
        var target=parts[5].split(':');
        var date=parts[6].split(':');
        var status=parts[8].split(':');
        var base=parts[4].split(':');
        var tr="<tr  onclick=choicePR(($(this).find(\"td[id='name']\").text()),($(this).find(\"td[id='branchTarget']\").text()),($(this).find(\"td[id='branchBase']\").text()),($(this).find(\"td[id='ID']\").text()));>";
        var td1="<td id=\"name\">"+username[1]+"</td>";
        var td2="<td id=\"ID\">"+ID[1]+"</td>";
        var td3="<td id=\"branchTarget\">"+target[1]+"</td>";
        var td4="<td id=\"branchBase\">"+base[1]+"</td>";
        var td5="<td>"+date[1]+"</td>";
        var td6="<td>"+status[1]+"</td></tr>";
        $("#PRlist").find('tbody').append(tr+td1+td2+td3+td4+td5+td6);
    });

}

function refreshRRBranches(branches) {
    $("#RRbrancheslist").empty();
    $.each(branches || [], function(index,branchname) {
        console.log("Adding user #" + index + ": " +  branchname);
            $('<li><a href="#" onclick=choiceBranchBase($(this).text()); >' + branchname + '</a></li>').appendTo($("#RRbrancheslist"));
        console.log(this);
    });
}

function refreshLRBranches(branches) {
    $("#LRbrancheslist").empty();
    $.each(branches || [], function(index,branchname) {
        console.log("Adding user #" + index + ": " +  branchname);
        $('<li><a href="#" onclick=choiceBranchTarget($(this).text()); >' + branchname + '</a></li>').appendTo($("#LRbrancheslist"));
        console.log(this);
    });
}

function ajaxChatContent() {
    $.ajax({
        url: CHAT_LIST_URL,
        data: "chatversion=" + chatVersion,
        dataType: 'json',
        timeOut: refreshRate,
        success: function(data) {
            console.log("Server chat version: " + data.version + ", Current chat version: " + chatVersion);
            if (data.version !== chatVersion) {
                chatVersion = data.version;
                appendToChatArea(data.entries);
            }
            triggerAjaxChatContent();
        },
        error: function(error) {
            triggerAjaxChatContent();
        }
    });
}

$(function() { // onload...do
    $("#chatform").submit(function() {
        $.ajax({
            data: $(this).serialize(),
            url: this.action,
            timeout: 2000,
            error: function() {
                console.error("Failed to submit");
            },
            success: function(r) {
            }
        });

        $("#userstring").val("");
        return false;
    });
});

function choicePR(name_,branchPurpose_,branchBase_,ID_ ){
    console.log(name_);
    console.log(branchPurpose_);
    console.log(branchBase_);
    console.log(ID_);
    window.open("../pr/pr.html?repo="+repo_name+"&name="+name_+"&branchPurpose="+branchPurpose_+"&branchBase="+branchBase_+"&ID="+ID_  ,"toolbar=yes,scrollbars=yes,resizable=yes,top=500,left=500,width=600,height=600");
}

function choiceBranchTarget(branchname){
    branch_target=branchname;
    console.log(branch_target);
    closeForm1();
    document.getElementById("myRR").style.display = "block";
    if (headBranchName===branch_target){
        $.ajax({
            url: PUSH_URL,
            type: "get",
            data: {repo:repo_name,
                RRusername:remoteRepo_username },
            success: function(data) {
                if (data.trim().toString() === "success"){
                    ajaxRRBranches();
                }
                else{
                    alert("branch exists on rr!");
                    return;

                }
            }
        });
    }
    else {
        alert("User cancelled the prompt.");
        return;
    }
   // ajaxRRBranches();
}

function choiceBranchBase(branchname){
    branch_base=branchname;
    console.log(branch_base);
    closeForm();
    var message = prompt("Enter Message of the pull request:");
    if (message == null || message == "") {
        alert("User cancelled the prompt.");
        return;}
    else {ajaxPR(message)};

}


function ajaxPRList(){

    $.ajax({
        url: PR_LIST_URL,
        type: "get",
        data: {repo: repo_name},
        success: function(data) {
            // [{"username: "shir","branchPurpose": koko","branchBase": new", "PRDate: 18:50:00 10.10.19","Status:open"},{....}]
            refreshPRList(data);
        }
    });
    setTimeout("ajaxPRList()",5000);
}

function ajaxpush(){
    $.ajax({
        url: PUSH_URL,
        type: "get",
        data: {repo:repo_name,
            RRusername:remoteRepo_username },
        success: function(data) {
            if (data.trim().toString() === "success"){
                return;
            }
              else{alert("branch exists on rr!")}
        }
    });
}

function updateDetailsOfRR(details){
    /*{"user name: shir","RRname: repo2"} */
    /*$.each(details || [], function(index, data) {
        console.log(index + ": " + data);
        remoteRepo_username=data["user name"];
        remoteRepo_name=data["RRname"];
    });*/
    //itamar repair
    //var data = JSON.parse(details);
    remoteRepo_username=details["user name"];
    remoteRepo_name=details["RRname"];



}

$(function() {
    setInterval(ajaxNotifications, refreshRate);
});






$(function() {
    repo_name=decodeURI(location.search.split('data=')[1]) ;
    repo_name=repo_name.trim();
    console.log(repo_name);
    $("#reponame").text("Repository name: "+repo_name);
    $.ajax({
        url: IS_RR_URL,
        type: "get",
        dataType: "json",
        data: {repo:repo_name},
    /*{"user name: shir","RRname: repo2"} */
        success: function(data) {
            remoteRepo_username=data["user name"];
            remoteRepo_name=data["RRname"];
            $("#RRname").text("RR Name: "+ remoteRepo_name+" RR User: " + remoteRepo_username);
            ajaxPRList();
            ajaxBranchesList();
        },
        error: function (thrownError) {
            remoteRepo_name=undefined;
        }
    });
});



$(function() {
    $.ajax({
        url: HEADBRANCH_URL,
        type: "get",
        data: {repo: repo_name},
        success: function(data) {
            /* {"deep"} */
            refreshHeadBranch(data);
            ajaxComTable();

        }

    });

});

$(function() {
    setInterval(ajaxChangesFiles(), refreshRate);
    ajaxCheckChanges();
    triggerAjaxChatContent();
});

$(function() {
    setInterval(ajaxPRList(), refreshRate);
});

$(function() {
    setInterval(ajaxBranchesList(), refreshRate);
});


setTimeout(function () {window.location.reload(1);},90000);



function triggerAjaxChatContent() {
    setTimeout(ajaxChatContent, refreshRate);
}



$(function() {
    $.ajax({
        url: USERNAME_URL,
        success: function(data) {
            user=data.trim();
            userNameDisplayRepo=data.trim();
            console.log("user" + user + ": " +"userNameDisplayRepo" +userNameDisplayRepo);
        }
    });
});





$(function() {
    $.ajax({
        url: USERNAME_URL,
        success: function(data) {
            user=data.trim();
            console.log("user" + user);
            ajaxBranchesList();
        }
    });
});


$(document).ready(function() {
$( "#newBranch" ).click(function(){
    var newBranchName = prompt("Enter branch name:");
    if (newBranchName == null || newBranchName == "") {
        alert("User cancelled the prompt.");}
    else {
        ajaxNewBranch(newBranchName);
    }
    });
});


$(document).ready(function() {
    $( "#checkOut" ).click(function(){
        var branchName;
        var branchName = prompt("Enter branch name to checkout:");
        if (branchName == null || branchName == "") {
            alert("User cancelled the prompt.");
            return;}
        if (branchName.includes("\\")){
            alert("The branch is a remote branch!");
            return;
        }
        ajaxCheckChanges();
        if (wereChanges==true){
            alert("You have pending changes!");
            return;}
        else {
            ajaxCheckOut(branchName);
        }
    });
});

$(document).ready(function() {
    $( "#showWC" ).click(function(){
        window.open("../wc/wc.html?repo="+repo_name, "_self","toolbar=yes,scrollbars=yes,resizable=yes,top=500,left=500,width=600,height=600");
    });
});

$(document).ready(function() {
    $( "#commit" ).click(function(){
        commitMessage = prompt("Enter commit message: ");
        if (commitMessage == null || commitMessage == "") {
            alert("User cancelled the prompt.");
            return;}
        ajaxCheckChanges();
        if (wereChanges==false){
            alert("You have pending changes!")
            return;
        }
        else {
            ajaxCommit();
        }
    });
});

$(document).ready(function() {
    $( "#pull" ).click(function(){
        if (remoteRepo_name==undefined){
            alert("RR doesn't exist! ");
            return;
        }
        ajaxCheckChanges();
        if (wereChanges==true){
            alert("You have pending changes!");
            return;
        }
        /*
        ajaxisRTB();
        if (isRTB==false){
            alert("The head branch is not a remote tracking branch!");
            return;
        }
        ajaxisHeadPushed();
        if (isHeadPushed==false){
            alert("There are unpushed changes on the head branch!");
            return;
        }

         */
        else{
            ajaxPull();}
    });
});

$(document).ready(function() {
    $( "#pushNewBranch" ).click(function(){
        if (remoteRepo_name==undefined){
            alert("RR doesn't exist! ");
            return;
        }
        ajaxpush();
    });
});





$(document).ready(function() {
    $( "#pr" ).click(function(){
        if (remoteRepo_name==undefined){
            alert("RR doesn't exist! ");
            return;
        }
        document.getElementById("myLR").style.display = "block";
        ajaxLRBranches();
        /*
        if (branch_base == undefined){
            alert("Not selected branch base!");
            return;
        }
        if (branch_purpose == undefined){
            alert("Not selected branch purpose!");
            return;
        }
        var message = prompt("Enter Message of the pull request:");
        if (message == null || message == "") {
            alert("User cancelled the prompt.");
            return;}
        else {ajaxPR(message)};

         */

    });
});

function closeForm() {
    document.getElementById("myRR").style.display = "none";
}

function closeForm1() {
    document.getElementById("myLR").style.display = "none";
}

function goBack() {
    window.open("../magit/magit.html?user="+user, "_self","toolbar=yes,scrollbars=yes,resizable=yes,top=500,left=500,width=600,height=600");
}

function openForm2() {
    document.getElementById("myChat").style.display = "block";
}

function closeForm4() {
    document.getElementById("myChat").style.display = "none";
}



