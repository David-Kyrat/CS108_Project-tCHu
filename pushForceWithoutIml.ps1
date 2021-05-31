git add .
git add --a

git restore --staged .\CS108_Project-tCHu.iml

[String] $message = Read-Host -Prompt "Enter Commit Message"

echo "======================= `r`nAdded Files to origin `r`n======================"

git commit -m $message

echo "======================= `r`nCommit Created `r`n=========================="

git pull 

git push --force

echo "====================== `r`nCommit pushed, End Script `r`n==================="
