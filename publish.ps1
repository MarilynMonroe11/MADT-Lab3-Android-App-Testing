# MADT Lab #3: publish a complete project to an EXISTING EMPTY GitHub repository.
# Run from PowerShell after extracting the project and creating an empty PUBLIC repo.
# Requires Git. Does not edit app source files or alter remote history.
param(
    [string]$ProjectDir = $PSScriptRoot,
    [string]$RepoUrl = 'https://github.com/MarilynMonroe11/MADT-Lab3-Android-App-Testing.git'
)

$ErrorActionPreference = 'Stop'
$OriginalLocation = Get-Location
function CheckGit($Description) {
    if ($LASTEXITCODE -ne 0) { throw "$Description (exit code $LASTEXITCODE)." }
}

try {
    if (-not (Get-Command git -ErrorAction SilentlyContinue)) {
        throw 'Git was not found. Install Git for Windows first.'
    }

    Set-Location -LiteralPath (Resolve-Path -LiteralPath $ProjectDir)
    foreach ($path in @('gradlew.bat','settings.gradle.kts','app/src/main','app/src/test','app/src/androidTest','gradle/wrapper/gradle-wrapper.jar','README.md','.gitignore')) {
        if (-not (Test-Path -LiteralPath $path)) { throw "Missing required project file/directory: $path" }
    }

    $props = Get-Content -LiteralPath 'gradle.properties' -Raw
    if ($props -notmatch '(?m)^android\.useAndroidX=true\s*$') {
        throw 'android.useAndroidX=true is not enabled. Do not publish this unverified configuration.'
    }
    if ((Get-Content -LiteralPath 'gradle/gradle-daemon-jvm.properties' -Raw) -notmatch '(?m)^toolchainVersion=21\s*$') {
        throw 'Gradle Daemon JDK 21 is not configured in this project.'
    }

    # Fail without altering existing remote history. An empty GitHub repo must be created in the browser first.
    $remoteRefs = & git ls-remote --heads $RepoUrl 2>&1
    CheckGit 'Cannot access the GitHub repository. Create it as an empty PUBLIC repository, and check network/authentication'
    if (($remoteRefs | Out-String).Trim().Length -gt 0) {
        throw 'The remote has branches already. Refusing to overwrite a non-empty repository.'
    }

    if (Test-Path -LiteralPath '.git') {
        throw 'This folder already contains a Git repository. Stop to avoid altering its history; use a clean extracted project.'
    }

    & git init -b main
    CheckGit 'git init failed'
    & git config user.name 'MarilynMonroe11'
    CheckGit 'Cannot configure Git user name'
    & git config user.email '324602932+MarilynMonroe11@users.noreply.github.com'
    CheckGit 'Cannot configure Git email'

    & git add --all
    CheckGit 'git add failed'
    Write-Host "`nStaged files to publish:" -ForegroundColor Cyan
    & git diff --cached --stat
    & git status --short
    $answer = Read-Host 'Publish the listed files to a PUBLIC GitHub repository? Type PUBLISH'
    if ($answer -cne 'PUBLISH') { Write-Host 'Cancelled before committing/pushing.'; return }

    & git commit -m 'MADT Lab 3: Android app, unit tests, UI tests and coverage setup'
    CheckGit 'git commit failed'
    & git remote add origin $RepoUrl
    CheckGit 'git remote add failed'
    & git push --set-upstream origin main
    CheckGit 'git push failed'
    Write-Host "`nPublished successfully: $($RepoUrl -replace '\.git$','')" -ForegroundColor Green
}
finally { Set-Location $OriginalLocation }
