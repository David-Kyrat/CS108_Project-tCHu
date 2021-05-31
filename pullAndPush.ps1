git add .
git add --a

[String] $message = Read-Host -Prompt "Enter Commit Message"

echo "======================= `r`nAdded Files to origin `r`n======================"

git commit -m $message

echo "======================= `r`nCommit Created `r`n=========================="

git pull 

git push

echo "====================== `r`nCommit pushed, End Script `r`n==================="
