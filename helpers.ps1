function Call($method, $path, $tok, $body) {
  $h = @{}
  if ($tok) { $h.Authorization = "Bearer $tok" }
  try {
    $p = @{ Method=$method; Uri="http://localhost:8080$path"; Headers=$h; ContentType="application/json" }
    if ($body) { $p.Body = ($body | ConvertTo-Json) }
    $r = Invoke-RestMethod @p
    Write-Host "OK   $method $path" -ForegroundColor Green
    $r
  } catch {
    $resp = $_.Exception.Response
    Write-Host ("{0} {1} {2}" -f [int]$resp.StatusCode, $method, $path) -ForegroundColor Yellow
    $text = $_.ErrorDetails.Message
    if (-not $text -and $resp -and $resp.GetResponseStream) {
      $text = (New-Object System.IO.StreamReader($resp.GetResponseStream())).ReadToEnd()
    }
    $text
  }
}

$login = { param($e) (Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/auth/login -ContentType "application/json" -Body (@{email=$e;password="password123"} | ConvertTo-Json)).token }

function Sql($query) {
  $query | docker exec -i helpdesk-postgres psql -U helpdesk_user -d helpdesk -P pager=off
}