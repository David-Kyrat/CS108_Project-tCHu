@echo off
::Powershell.exe -noprofile  -executionpolicy remotesigned -File .\run-tchu.ps1
::pause

PowerShell -NoProfile -ExecutionPolicy Bypass -Command "& './run-tchu.ps1'" 

