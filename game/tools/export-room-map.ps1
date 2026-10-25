param(
    [Parameter(Mandatory = $true)][string]$Source,
    [Parameter(Mandatory = $true)][string]$Destination
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing
$taskSourcePath = $ExecutionContext.SessionState.Path.GetUnresolvedProviderPathFromPSPath($Source)
$taskDestinationPath = $ExecutionContext.SessionState.Path.GetUnresolvedProviderPathFromPSPath($Destination)
if ($taskSourcePath -eq $taskDestinationPath) { throw 'Export requires a separate source and destination.' }

$taskImage = [Drawing.Image]::FromFile($taskSourcePath)
$taskBitmap = $null
$taskGraphics = $null
try {
    if ($taskImage.Width * 3 -ne $taskImage.Height * 4) { throw 'Room source must have a 4:3 aspect ratio.' }
    $taskBitmap = [Drawing.Bitmap]::new(640, 480, [Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $taskGraphics = [Drawing.Graphics]::FromImage($taskBitmap)
    $taskGraphics.InterpolationMode = [Drawing.Drawing2D.InterpolationMode]::NearestNeighbor
    $taskGraphics.PixelOffsetMode = [Drawing.Drawing2D.PixelOffsetMode]::Half
    $taskGraphics.SmoothingMode = [Drawing.Drawing2D.SmoothingMode]::None
    $taskGraphics.CompositingMode = [Drawing.Drawing2D.CompositingMode]::SourceCopy
    $taskGraphics.DrawImage($taskImage, [Drawing.Rectangle]::new(0, 0, 640, 480), 0, 0,
        $taskImage.Width, $taskImage.Height, [Drawing.GraphicsUnit]::Pixel)
    [IO.Directory]::CreateDirectory([IO.Path]::GetDirectoryName($taskDestinationPath)) | Out-Null
    $taskBitmap.Save($taskDestinationPath, [Drawing.Imaging.ImageFormat]::Png)
    Write-Output "Exported 640x480 RGBA PNG with nearest-neighbor sampling: $taskDestinationPath"
}
finally {
    if ($null -ne $taskGraphics) { $taskGraphics.Dispose() }
    if ($null -ne $taskBitmap) { $taskBitmap.Dispose() }
    $taskImage.Dispose()
}
