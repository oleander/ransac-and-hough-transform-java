def c(a,b,r); tt = t; [(a + r * Math.cos(tt)).to_i, (b + r * Math.sin(tt)).to_i]; end
def t; rand(100) / 100.0 * 2 * Math::PI; end
raw = 30.times.map { c(10, 20, 30).join(",") }.join("\n")
File.open(file, "w") { |file| file.write(raw) }
