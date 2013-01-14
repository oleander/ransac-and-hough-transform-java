PERCENT = 30
def mess(x); x + (x * rand(PERCENT) / 100.0) * [-1, 1].sample; end
def c(a,b,r); tt = t; [(a + r * Math.cos(tt)).round.to_i, (b + r * Math.sin(tt)).round.to_i]; end
def t; rand(100) / 100.0 * 2 * Math::PI; end
raw = 30.times.map do 
  x, y = c(100, 200, 300)
  [mess(x).round.to_i, mess(y).round.to_i].join(",") 
end.join("\n")
File.open("pc.data", "w") { |file| file.write(raw) }
